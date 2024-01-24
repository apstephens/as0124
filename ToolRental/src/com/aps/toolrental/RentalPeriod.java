package com.aps.toolrental;

/*
 * Class that holds the count of weekdays, weekend days, and holidays for a rental period
 */
class RentalPeriod {
	private final int weekdays;
	private final int weekendDays;
	private final int holidays;
	

	RentalPeriod(int weekdays, int weekendDays, int holidays ) {
		this.weekdays = weekdays;
		this.weekendDays = weekendDays;
		this.holidays = holidays;
	}
	
	/*
	 * Accessors
	 */
	int getWeekdays() {
		return weekdays;
	}

	int getWeekendDays() {
		return weekendDays;
	}

	int getHolidays() {
		return holidays;
	}
}
