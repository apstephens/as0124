/**
 * 
 */
package com.aps.toolrental;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Class that implements the physical data loader for the HolidaySpec collection
 * in the RentalCalendar class.
 * 
 * This particular implementation reads its configuration from property files on
 * the classpath. It could be replaced with something that reads XML, JSON
 * documents, or even "hardcoded" static instance variables.
 * 
 * Property files were chosen because the data was "tabular" in nature, and
 * short (and simple). Since Java natively supports "properties", there are no
 * dependencies upon external libraries (ala "Jackson" for JSON, etc.)
 */
class HolidaySpecLoader {
	// Constant Reference Data
	private static final String HOLIDAY_PROPERTIES_FILE = "src/resources/holidays.properties";
	private static final String HOLIDAY_LIST = "holidayList";
	private static final String HOLIDAY_TYPE_PROP = "holidayType";
	private static final String HOLIDAY_NAME_PROP = "holidayName";
	private static final String HOLIDAY_MONTH_PROP = "month";
	private static final String HOLIDAY_DAY_PROP = "day";
	private static final String HOLIDAY_ADJUST_WEEKEND_PROP = "adjustWeekend";
	private static final String HOLIDAY_DAY_OF_WEEK_PROP = "dayOfWeek";
	private static final String HOLIDAY_ORDINAL_WEEK_PROP = "ordinalWeek";
	private static final int HOLIDAY_MAX_ORDINAL_WEEK = 4;

	// Error messages
	private static final String ERROR_INVALID_HOLIDAY_TYPE = " does not contain 'holidayType' (FIXED or FLOATING): ";
	private static final String ERROR_INVALID_MONTH_NAME = " does not contain a valid month name: ";
	private static final String ËRROR_DAY_OUT_OF_RANGE = " is out of range for the specified month: ";
	private static final String ERROR_DAY_OF_WEEK = " is not a valid day of the week name: ";
	private static final String ERROR_ORDINAL_OUT_OF_RANGE = " is out of range for the number of weeks in a month: ";

	/**
	 * Reads the "holidays.properties" file to populate the collection of holiday
	 * specs
	 * 
	 * @return Set of HolidaySpec subclass object instances
	 * @throws Exception
	 */
	protected static Set<HolidaySpec> initializeHolidaySpecs() throws Exception {
		// Load the properties file
		Properties holidayProps = DataLoadPropertiesHelper.readConfigFile(HOLIDAY_PROPERTIES_FILE);

		// Find the list of holidays to be read
		String holidayList = DataLoadPropertiesHelper.getCollectionKeyProperty(holidayProps, HOLIDAY_LIST,
				HOLIDAY_PROPERTIES_FILE);

		// OK, at least there's a property matching the property list. So now allocate
		// the storage for it
		Set<HolidaySpec> holidaySpecSet = new HashSet<HolidaySpec>();

		// Parse the list (comma separated values)
		String[] holidays = holidayList.split(",");
		for (String holidayIdx : holidays) {
			String holiday = holidayIdx.trim();
			// Find additional values
			String holidayTypeStr = DataLoadPropertiesHelper.getChildProperty(holidayProps, holiday, HOLIDAY_TYPE_PROP,
					HOLIDAY_PROPERTIES_FILE);
			HolidayType holidayType = null;
			try {
				holidayType = HolidayType.valueOf(holidayTypeStr);
			} catch (IllegalArgumentException e) {
				throw new Exception(
						holiday + "." + HOLIDAY_TYPE_PROP + ERROR_INVALID_HOLIDAY_TYPE + HOLIDAY_PROPERTIES_FILE, e);
			}
			HolidaySpec holidaySpec = new HolidaySpec();
			holidaySpec.setHolidayType(holidayType);

			// Need Month loaded first, so that day can be validated for "fixed" type
			loadCommonProperties(holidaySpec, holidayProps, holiday);
			if (holidayType == HolidayType.FIXED) {
				loadFixedHolidayProperties(holidaySpec, holidayProps, holiday);
			} else {
				loadFloatingHolidayProperties(holidaySpec, holidayProps, holiday);
			}
			holidaySpecSet.add(holidaySpec);
		}
		return holidaySpecSet;
	}

	/*
	 * Load the properties common to all HolidaySpec HolidayTypes.
	 */
	private static void loadCommonProperties(HolidaySpec spec, Properties holidayProps, String parentProp)
			throws Exception {
		// Load the holiday name
		String nameStr = DataLoadPropertiesHelper.getChildProperty(holidayProps, parentProp, HOLIDAY_NAME_PROP,
				HOLIDAY_PROPERTIES_FILE);
		spec.setName(nameStr);
		String monthStr = DataLoadPropertiesHelper.getChildProperty(holidayProps, parentProp, HOLIDAY_MONTH_PROP,
				HOLIDAY_PROPERTIES_FILE);
		try {
			Month month = Month.valueOf(monthStr);
			spec.setMonth(month);
		} catch (IllegalArgumentException e) {
			throw new Exception(
					parentProp + "." + HOLIDAY_MONTH_PROP + ERROR_INVALID_MONTH_NAME + HOLIDAY_PROPERTIES_FILE, e);
		}
	}

	/*
	 * Load the properties specific to the "FIXED" HolidayType specs
	 */
	private static void loadFixedHolidayProperties(HolidaySpec spec, Properties holidayProps, String parentProp)
			throws Exception {
		// Load the day attribute
		String dayStr = DataLoadPropertiesHelper.getChildProperty(holidayProps, parentProp, HOLIDAY_DAY_PROP,
				HOLIDAY_PROPERTIES_FILE);
		int day = DataLoadPropertiesHelper.convertIntegerString(dayStr, parentProp + "." + HOLIDAY_DAY_PROP,
				HOLIDAY_PROPERTIES_FILE);
		// And make sure it's in range for the month.
		if (day < 1 || day > spec.getMonth().maxLength()) {
			throw new Exception(parentProp + "." + HOLIDAY_DAY_PROP + ËRROR_DAY_OUT_OF_RANGE + HOLIDAY_PROPERTIES_FILE);
		}
		spec.setDay(day);

		// Load the weekendAdjust attribute
		String adjustWeekendStr = DataLoadPropertiesHelper.getChildProperty(holidayProps, parentProp,
				HOLIDAY_ADJUST_WEEKEND_PROP, HOLIDAY_PROPERTIES_FILE);
		boolean adjustWeekend = DataLoadPropertiesHelper.convertBooleanString(adjustWeekendStr,
				parentProp + "." + HOLIDAY_ADJUST_WEEKEND_PROP, HOLIDAY_PROPERTIES_FILE);
		spec.setAdjustWeekend(adjustWeekend);
	}

	/*
	 * Load the properties specific to "FLOATING" HolidayType specs
	 */
	private static void loadFloatingHolidayProperties(HolidaySpec spec, Properties holidayProps, String parentProp)
			throws Exception {
		// Load the DayOfWeek property
		String dayOfWeekStr = DataLoadPropertiesHelper.getChildProperty(holidayProps, parentProp,
				HOLIDAY_DAY_OF_WEEK_PROP, HOLIDAY_PROPERTIES_FILE);
		try {
			DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
			spec.setDayOfWeek(dayOfWeek);
		} catch (IllegalArgumentException e) {
			throw new Exception(
					parentProp + "." + HOLIDAY_DAY_OF_WEEK_PROP + ERROR_DAY_OF_WEEK + HOLIDAY_PROPERTIES_FILE, e);
		}

		// Load the Ordinal Week property
		String ordinalWkStr = DataLoadPropertiesHelper.getChildProperty(holidayProps, parentProp,
				HOLIDAY_ORDINAL_WEEK_PROP, HOLIDAY_PROPERTIES_FILE);
		int ordinalWeek = DataLoadPropertiesHelper.convertIntegerString(ordinalWkStr,
				parentProp + "." + HOLIDAY_ORDINAL_WEEK_PROP, HOLIDAY_PROPERTIES_FILE);
		// Check to see if it's in range
		if (ordinalWeek < 1 || ordinalWeek > HOLIDAY_MAX_ORDINAL_WEEK) {
			throw new Exception(parentProp + "." + HOLIDAY_ORDINAL_WEEK_PROP + ERROR_ORDINAL_OUT_OF_RANGE
					+ HOLIDAY_PROPERTIES_FILE);
		}
		spec.setOrdinalWeek(ordinalWeek);
	}
}
