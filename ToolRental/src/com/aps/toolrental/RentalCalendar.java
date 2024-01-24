package com.aps.toolrental;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static java.time.temporal.TemporalAdjusters.dayOfWeekInMonth;

/**
 * Singleton class that assists with calendar functions for rental calculations
 * All methods are static
 * weekdays, weekends, holidays for a specific period
 */
class RentalCalendar {
	// Instance variable
	private static RentalCalendar instance = null;
	// Accessor for general configuration
	private AppConfig appConfig = null;
	
	// Holds the holiday definitions as specified in "holiday.properties"
	// Note:  the contained HolidaySpec instances are not immutable.  Use
	// care when accessing--there is no need to use the setters.
	private Set<HolidaySpec> holidaySpecs = null;
	
	// Cache of previously calculated holidays for year.
	private HashMap<Integer, List<LocalDate>> holidayCache = new HashMap<Integer, List<LocalDate>>();

	// Error Messages
	private static final String ERROR_HOLIDAY_INITIALIZATION = "Unable to initialize " 
			+ HolidaySpec.class.getName() + " collection. Cause:";
	
	/*
	 * Private constructor
	 * Loads the holiday specs into memory
	 */
	private RentalCalendar() {
		try {
			setHolidaySpecs(HolidaySpecLoader.initializeHolidaySpecs());
		} catch (Exception e) {
			System.out.println(ERROR_HOLIDAY_INITIALIZATION);
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
	
	private AppConfig getAppConfig() {
		if (appConfig == null) {
			setAppConfig(AppConfig.getInstance());
		}
		return appConfig;
	}
	
	private Set<HolidaySpec> getHolidaySpecs() {
		return holidaySpecs;
	}
	
	private void setHolidaySpecs(Set<HolidaySpec> holidaySpecs) {
		this.holidaySpecs = holidaySpecs;
	}
	
	/*
	 * Returns the collection of holidays for a year
	 * If they haven't been calculated yet, calculate then cache them
	 * (only need to do this once per input 'year', for efficiency's sake)
	 */
	private List<LocalDate> getHolidays(int year) {
		Integer iYear = Integer.valueOf(year);
		List<LocalDate> holidays = holidayCache.get(iYear);
		if (holidays == null) {
			// Only need to calculate the holidays for a year at one time
			holidays = calculateHolidays(year);
			holidayCache.put(iYear, holidays);
		}
		return holidays;
	}

	/*
	 * Calculates the holidays for an entire year, based on 
	 * HolidaySpec.holidayType
	 */
	private List<LocalDate> calculateHolidays(int year) {
		// Get the holiday specification rules
		Set<HolidaySpec> holidaySpecs = getHolidaySpecs();
		List<LocalDate> holidays = new ArrayList<LocalDate>();
		
		Iterator<HolidaySpec> iter = holidaySpecs.iterator();
		while (iter.hasNext()) {
			HolidaySpec spec = iter.next();
			LocalDate holidayDate = null;
			switch (spec.getHolidayType()) {
			case FIXED:
				holidayDate = calculateFixedHoliday(spec, year);
				break;
			case FLOATING:
				holidayDate = calculateFloatingHoliday(spec, year);
				break;
			}
			holidays.add(holidayDate);
		}
		return holidays;
	}
	
	/*
	 * Calculate a fixded-day holiday.  Adjust of the weekend, if necessary
	 */
	private LocalDate calculateFixedHoliday(HolidaySpec spec, int year) {
		LocalDate holiday = LocalDate.of(year, spec.getMonth(), spec.getDay());
		// Check to see if it slides on weekends
		if (spec.isAdjustWeekend()) {
			// Is the date on a weekend day?
			DayOfWeek holDoW = holiday.getDayOfWeek();
			if (getAppConfig().getWeekends().contains(holDoW)) {
				if (holDoW.compareTo(getAppConfig().getWeekendStart())  == 0 ) {
					// Slide back
					holiday = holiday.minusDays(1);
				} else {
					// Slide forward
					holiday = holiday.plusDays(1);
				}
			}
		}
		return holiday;
	}

	/*
	 * Calculate a floating holiday.
	 */
	private LocalDate calculateFloatingHoliday(HolidaySpec spec, int year) {
		LocalDate firstOfMonth = LocalDate.of(year, spec.getMonth(), 1);
		LocalDate holiday = firstOfMonth.with(dayOfWeekInMonth(spec.getOrdinalWeek(), spec.getDayOfWeek()));
		return holiday;
	}


	/**
	 * Returns an instance of the RentalCalendar.
	 * 
	 * @return
	 * @throws Exception
	 */
	static RentalCalendar getInstance(){
		if (instance == null) {
			instance = new RentalCalendar();
		}
		return instance;
	}

	/**
	 * Qualify the rental period's number of weekdays, weekend days, and holidays for 
	 * specific start date and duration
	 * 
	 * @param startDate
	 * @param numDays
	 * @return RentalPeriod instance
	 */
	RentalPeriod calculateRentalPeriod(LocalDate startDate, int numDays) {
		int weekdays = 0;
		int weekendDays = 0;
		int holidays = 0;
		
		// Sort of a brute-force approach, but you must visit each calendar day
		// of the period to determine its type.
		LocalDate currDate = startDate;
		int currYear = startDate.getYear();
		List<LocalDate> currHolidays = getHolidays(currYear);
		for (int i = 0; i < numDays; i++) {
			// Holiday takes precedence over Weekend, if they fall on the same
			// day and there is no adjustment for the Holiday off of the weekend
			if (currHolidays.contains(currDate)) {
				holidays++;
			} else if (getAppConfig().getWeekends().contains(currDate.getDayOfWeek())) {
				weekendDays++;
			} else {
				weekdays++;
			}
			currDate = currDate.plusDays(1);
			if (currYear != currDate.getYear()) {
				currYear = currDate.getYear();
				currHolidays = getHolidays(currYear);
			}
		}
		return new RentalPeriod(weekdays, weekendDays, holidays);
	}
}