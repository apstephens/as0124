package com.aps.toolrental;

import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Properties;

/**
 * Expresses the general configuration rules for the application. Application
 * locale, date format, decimal precision and rounding rules should be set here.
 *
 * Additional configuration properties may be added as required per the
 * implementation chosen for reading/initializing reference data.
 * 
 * This is a singleton class with lazy initialization
 */
class AppConfig {
	// Initialize upon instantiation
	private static AppConfig instance = null;

	// Member variables
	// Set defaults (may be overwritten by the config.properties values, if they
	// exist)
	private Locale locale = Locale.US;
	private String dateFormat = "MM/dd/yy";
	private EnumSet<DayOfWeek> weekends = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
	private DayOfWeek weekendStart = DayOfWeek.SATURDAY;
	private int scale = DEFAULT_SCALE;
	private RoundingMode roundingMode = DEFAULT_ROUNDING;

	// Static Module definitions
	private static final int DEFAULT_SCALE = 2;
	private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;
	private static final String DEFAULT_LANG = "en";
	private static final String DEFAULT_COUNTRY = "US";
	private static final String CONFIG_PROPERTIES_FILE = "src/resources/config.properties";
	private static final String LANGUAGE_PROP = "language";
	private static final String COUNTRY_PROP = "country";
	private static final String DATE_FORMAT_PROP = "dateFormat";
	private static final String WEEKEND_DAYS_PROP = "weekendDays";
	private static final String DECIMAL_SCALE_PROP = "decimalScale";
	private static final String ROUNDING_RULE_PROP = "roundingMode";

	// Errors
	private static final String ERROR_CANT_INITIALIZE = "Unable to initialize " + AppConfig.class.getName()
			+ ". Cause:";
	private static final String ERROR_ILLEGAL_DATE_FORMAT = " does not contain a legal date value in: ";
	private static final String ERROR_ILLEGAL_DAY_NAME = " is not a legal day name for 'weekend_days' "
			+ "property in : ";
	private static final String ERROR_ILLEGAL_ROUNDING_MODE = " is not a rounding rule for 'rounding_rule' "
			+ "property in : ";

	/**
	 * Private constructor for initialization.
	 */
	private AppConfig() {
		try {
			readConfigurationProperties(CONFIG_PROPERTIES_FILE);
		} catch (Exception e) {
			System.out.println(ERROR_CANT_INITIALIZE);
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads the application configuration from a properties file in the classpath
	 * If the file is not found, the application continues with the defaults
	 * 
	 * @param configPropertiesFile
	 */
	private void readConfigurationProperties(String configPropertiesFile) throws Exception {
		Properties props = DataLoadPropertiesHelper.readConfigFile(configPropertiesFile, false);
		if (props != null) {
			processConfigurationProperties(props);
		}
	}

	/**
	 * Wrapper to deal with all props in the file. Individual methods are called to
	 * set properties for locale, calendar specification (weekend days), and math
	 * scale and rounding.
	 * 
	 * If any property is malformed, then Exception is thrown. If it is missing,
	 * then the default value is used.
	 * 
	 * If additional properties are to be configured in the future, they should be
	 * added here.
	 * 
	 * @param props
	 * @throws Exception
	 */
	private void processConfigurationProperties(Properties props) throws Exception {
		processLocaleProperties(props);
		processCalendarProperties(props);
		processDecimalManagementProperties(props);
	}

	/**
	 * Read any locale properties from the configuration file and set the locale
	 * accordingly
	 * 
	 * @param props
	 */
	private void processLocaleProperties(Properties props) {
		// java.util.Locale does not have a very smart constructor.
		// Only throws an exception for null input, so we'll avoid those.
		// Otherwise, there's no checking.
		String language = props.getProperty(LANGUAGE_PROP, DEFAULT_LANG);
		String country = props.getProperty(COUNTRY_PROP, DEFAULT_COUNTRY);
		Locale locale = new Locale(language, country);
		setLocale(locale);
	}

	/**
	 * Read any calendar related properties (date format, weekend days) and set the
	 * associated configuration accordingly
	 * 
	 * @param props
	 * @throws Exception
	 */
	private void processCalendarProperties(Properties props) throws Exception {
		// Process date format
		String dateFormat = props.getProperty(DATE_FORMAT_PROP);
		// Test the format
		try {
			@SuppressWarnings("unused")
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat, getLocale());
		} catch (Exception e) {
			throw new Exception(DATE_FORMAT_PROP + ERROR_ILLEGAL_DATE_FORMAT + CONFIG_PROPERTIES_FILE, e);
		}
		setDateFormat(dateFormat);

		// Process Weekend days
		String weekendDayProp = props.getProperty(WEEKEND_DAYS_PROP);
		if (weekendDayProp != null) {
			EnumSet<DayOfWeek> weekends = EnumSet.noneOf(DayOfWeek.class);

			// Attempt to convert the strings to legal days
			String[] weekendDayNames = weekendDayProp.split(",");
			DayOfWeek weekendStart = null;
			for (String dayName : weekendDayNames) {
				try {
					DayOfWeek day = DayOfWeek.valueOf(dayName);
					weekends.add(day);
					if (weekendStart == null) {
						weekendStart = day;
					}
				} catch (IllegalArgumentException e) {
					throw new Exception(dayName + ERROR_ILLEGAL_DAY_NAME + CONFIG_PROPERTIES_FILE, e);
				}
			}
			setWeekends(weekends);
			setWeekendStart(weekendStart);
		}
	}

	private void processDecimalManagementProperties(Properties props) throws Exception {
		// Process decimal scale
		String scaleStr = props.getProperty(DECIMAL_SCALE_PROP);
		if (scaleStr != null) {
			int scale = DataLoadPropertiesHelper.convertIntegerString(scaleStr, DECIMAL_SCALE_PROP,
					CONFIG_PROPERTIES_FILE);
			setScale(scale);
		}

		// Process Rounding mode
		String roundingModeStr = props.getProperty(ROUNDING_RULE_PROP);
		if (roundingModeStr != null) {
			 roundingMode = DEFAULT_ROUNDING;
			try {
				RoundingMode roundingMode = RoundingMode.valueOf(roundingModeStr);
				setRoundingMode(roundingMode);
			} catch (IllegalArgumentException e) {
				throw new Exception(roundingModeStr + ERROR_ILLEGAL_ROUNDING_MODE + CONFIG_PROPERTIES_FILE, e);
			}
		}
	}

	/*
	 * Private Setters. Used by the individual configuration properties being read
	 * from the file
	 */

	private void setLocale(Locale locale) {
		this.locale = locale;
	}

	private void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	private void setWeekends(EnumSet<DayOfWeek> weekends) {
		this.weekends = weekends;
	}

	private void setWeekendStart(DayOfWeek weekendStart) {
		this.weekendStart = weekendStart;
	}

	private void setScale(int scale) {
		this.scale = scale;
	}

	private void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	/*
	 * Accessor Methods
	 */
	static AppConfig getInstance() {
		if (instance == null) {
			instance = new AppConfig();
		}
		return instance;
	}

	Locale getLocale() {
		return locale;
	}

	String getDateFormat() {
		return dateFormat;
	}

	EnumSet<DayOfWeek> getWeekends() {
		return weekends;
	}

	DayOfWeek getWeekendStart() {
		return weekendStart;
	}
	
	int getScale() {
		return scale;
	}
	
	RoundingMode getRoundingMode() {
		return roundingMode;
	}
}
