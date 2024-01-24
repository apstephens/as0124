package com.aps.toolrental;

import java.time.DayOfWeek;
import java.time.Month;

/**
 * POJO Class that holds the definition of a holiday.
 * 
 * There are two types of holidays in general use: FIXED and FLOATING
 *
 * Fixed holidays are on specific days of the month, with a possible
 * allowance of sliding to nearest weekday if the holiday falls on a
 * weekend.
 * 
 * Floating holidays dates vary within month,but they are tied to a 
 * specific day of week and ordinal week of the month, e.g.: 
 * 3rd Monday, etc.
 * 
 * Not all attributes are populated.  Only the ones relevant to the
 * holiday type are set.
 * 
 * Though this holds "reference" data, it is not immutable. However,
 * the only consumer is the "RentalCalendar" class, so care will be exercised
 * to only read from the instantiated objects.
 * 
 */
class HolidaySpec {
	private HolidayType holidayType = null;
	private String name = null;
	private Month month = null;
	private int day = 0;
	private boolean adjustWeekend = false;
	private DayOfWeek dayOfWeek = null;
	private int ordinalWeek = 0;

	// Empty Constructor
	HolidaySpec() {
	}

	/*
	 * Accessor Methods
	 */
	HolidayType getHolidayType() {
		return holidayType;
	}

	void setHolidayType(HolidayType holidayType) {
		this.holidayType = holidayType;
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	Month getMonth() {
		return month;
	}

	void setMonth(Month month) {
		this.month = month;
	}

	int getDay() {
		return day;
	}

	void setDay(int day) {
		this.day = day;
	}

	boolean isAdjustWeekend() {
		return adjustWeekend;
	}

	void setAdjustWeekend(boolean adjustWeekend) {
		this.adjustWeekend = adjustWeekend;
	}

	DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	int getOrdinalWeek() {
		return ordinalWeek;
	}

	void setOrdinalWeek(int ordinalWeek) {
		this.ordinalWeek = ordinalWeek;
	}

}
