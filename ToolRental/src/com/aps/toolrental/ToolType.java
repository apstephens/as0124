package com.aps.toolrental;

import java.math.BigDecimal;

/**
 * Simple POJO to model ToolType definitions. Object is immutable after
 * instantiation since it holds "reference" data and is publicly visible
 */
public class ToolType {
	private final String toolType;
	private final BigDecimal dailyCharge;
	private final boolean weekdayCharge;
	private final boolean weekendCharge;
	private final boolean holidayCharge;

	/**
	 * Constructor to initialize "final" instance variables
	 */
	public ToolType(String toolType, BigDecimal dailyCharge, boolean weekdayCharge, boolean weekendCharge,
			boolean holidayCharge) {
		this.toolType = toolType;
		this.dailyCharge = dailyCharge;
		this.weekdayCharge = weekdayCharge;
		this.weekendCharge = weekendCharge;
		this.holidayCharge = holidayCharge;
	}

	public String getToolType() {
		return toolType;
	}

	public BigDecimal getDailyCharge() {
		return dailyCharge;
	}

	public boolean hasWeekdayCharge() {
		return weekdayCharge;
	}

	public boolean hasWeekendCharge() {
		return weekendCharge;
	}

	public boolean hasHolidayCharge() {
		return holidayCharge;
	}
}
