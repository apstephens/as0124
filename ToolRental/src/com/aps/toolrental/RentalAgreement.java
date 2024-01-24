package com.aps.toolrental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

/**
 * Creates a rental agreement for a tool
 */
public class RentalAgreement {
	// Member variables
	private Tool tool = null;
	private ToolType toolType = null;
	private LocalDate checkoutDate = null;
	private LocalDate dueDate = null;
	private int rentalDays = 0;
	private int chargeDays = 0;
	private BigDecimal preDiscountCharge = null;
	private double discountPercent = 0;
	private BigDecimal discountAmount = null;
	private BigDecimal finalCharge = null;

	// Truly private member variable
	private AppConfig appConfig = null;
	private ToolCatalog catalog = null;
	private RentalCalendar calendar = null;

	// Validation Error Messages
	private static final String ERROR_INVALID_TOOLCODE = "There is no tool with toolcode: ";
	private static final String ERROR_TOOLCODE_NULL = "toolCode parameter cannot be null.";
	private static final String ERROR_CHECKOUT_DATE_NULL = "checkoutDate cannot be null.";
	private static final String ERROR_RENTAL_DAYS = "Rental period must be at least one day.";
	private static final String ERROR_PERCENTAGE = "Discount must be a valid percentage between 0 and 100.";

	// Display labels
	private static final String TOOL_CODE = "Tool code: ";
	private static final String TOOL_TYPE = "Tool type: ";
	private static final String TOOL_BRAND = "Tool brand: ";
	private static final String RENTAL_DAYS = "Rental days: ";
	private static final String CHECKOUT_DATE = "Checkout date: ";
	private static final String DUE_DATE = "Due date: ";
	private static final String DAILY_CHARGE = "Daily rental charge: ";
	private static final String CHARGE_DAYS = "Charge days: ";
	private static final String PRE_DISCOUNT_CHARGE = "Pre-discount charge: ";
	private static final String DISCOUNT_PERCENT = "Discount percent: ";
	private static final String DISCOUNT_AMOUNT = "Discount amount: ";
	private static final String FINAL_CHARGE = "Final Charge: ";

	public RentalAgreement() {
		// Initialize access to reference data
		setAppConfig(AppConfig.getInstance());
		setCatalog(ToolCatalog.getInstance());
		setCalendar(RentalCalendar.getInstance());
	}

	/*
	 * Private member accessors
	 */

	private void setTool(Tool tool) {
		this.tool = tool;
	}

	private void setToolType(ToolType toolType) {
		this.toolType = toolType;
	}

	private void setCheckoutDate(LocalDate checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	private void setDueDate(LocalDate checkoutDate) {
		this.dueDate = checkoutDate;
	}

	private void setRentalDays(int rentalDays) {
		this.rentalDays = rentalDays;
	}

	private void setChargeDays(int chargeDays) {
		this.chargeDays = chargeDays;
	}

	private void setPreDiscountCharge(BigDecimal preDiscountCharge) {
		this.preDiscountCharge = preDiscountCharge;
	}

	private void setDiscountPercent(double discountPercent) {
		this.discountPercent = discountPercent;
	}

	private void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	private void setFinalCharge(BigDecimal finalCharge) {
		this.finalCharge = finalCharge;
	}

	private void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	private void setCatalog(ToolCatalog catalog) {
		this.catalog = catalog;
	}

	private void setCalendar(RentalCalendar calendar) {
		this.calendar = calendar;
	}

	private AppConfig getAppConfig() {
		return appConfig;
	}

	private ToolCatalog getCatalog() {
		return catalog;
	}

	private RentalCalendar getCalendar() {
		return calendar;
	}

	/*
	 * Public Accessors
	 */

	public Tool getTool() {
		return tool;
	}

	public ToolType getToolType() {
		return toolType;
	}

	public LocalDate getCheckoutDate() {
		return checkoutDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public int getRentalDays() {
		return rentalDays;
	}

	public int getChargeDays() {
		return chargeDays;
	}

	public BigDecimal getPreDiscountCharge() {
		return preDiscountCharge;
	}

	public double getDiscountPercent() {
		return discountPercent;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public BigDecimal getFinalCharge() {
		return finalCharge;
	}

	/*
	 * Public Methods--there are only two: checkout and printAgreement
	 */
	/**
	 * Completes a rental agreement. Inputs are validated, and if out of range, an
	 * IllegalArgumentException is generated If all inputs are valid, the rental
	 * charges, charge days, and discounts are calculated.
	 * 
	 * @param toolCode
	 * @param checkoutDate
	 * @param rentalDayCount
	 * @param discountPercent
	 * @return RentalAgreement
	 * @throws IllegalArgumentException
	 */
	public static RentalAgreement checkout(String toolCode, LocalDate checkoutDate, int rentalDayCount,
			int discountPercent) throws IllegalArgumentException {
		RentalAgreement agreement = new RentalAgreement();
		// Validate inputs and set member variables when successful
		agreement.setTool(validateToolCode(toolCode));
		agreement.setToolType(agreement.getCatalog().getToolType(agreement.getTool().getToolType()));
		agreement.setCheckoutDate(validateCheckoutDate(checkoutDate));
		agreement.setRentalDays(validateRentalDayCount(rentalDayCount));
		agreement.setDiscountPercent(validateDiscountPercentage(discountPercent));

		// Calculate due date
		agreement.setDueDate(checkoutDate.plusDays(rentalDayCount));

		// Calculate Rental Period
		RentalPeriod period = agreement.getCalendar().calculateRentalPeriod(checkoutDate, rentalDayCount);

		// Calculate charge days
		agreement.setChargeDays(calculateChargeDays(agreement.getToolType(), period));

		int scale = agreement.getAppConfig().getScale();
		RoundingMode mode = agreement.getAppConfig().getRoundingMode();

		// Calculate pre-discount charge
		BigDecimal dailyCharge = agreement.getToolType().getDailyCharge();
		BigDecimal bdDays = new BigDecimal(agreement.getChargeDays());
		BigDecimal preDiscountCharge = dailyCharge.multiply(bdDays);
		agreement.setPreDiscountCharge(preDiscountCharge);

		// Calculate discount percentage
		BigDecimal bdPct = new BigDecimal(agreement.getDiscountPercent()).setScale(scale, mode);
		BigDecimal discountAmount = preDiscountCharge.multiply(bdPct).setScale(scale, mode);
 		agreement.setDiscountAmount(discountAmount);

		// Calculate final charge
		agreement.setFinalCharge(preDiscountCharge.subtract(discountAmount));

		return agreement;
	}

	/*
	 * input validators
	 */
	private static Tool validateToolCode(String toolCode) throws IllegalArgumentException {
		if (toolCode == null) {
			throw new IllegalArgumentException(ERROR_TOOLCODE_NULL);
		}
		Tool tool = ToolCatalog.getInstance().getTool(toolCode);
		if (tool == null) {
			throw new IllegalArgumentException(ERROR_INVALID_TOOLCODE + toolCode);
		}
		return tool;
	}

	private static LocalDate validateCheckoutDate(LocalDate checkoutDate) throws IllegalArgumentException {
		if (checkoutDate == null) {
			throw new IllegalArgumentException(ERROR_CHECKOUT_DATE_NULL);
		}
		return checkoutDate;
	}

	private static int validateRentalDayCount(int rentalDayCount) throws IllegalArgumentException {
		if (rentalDayCount < 1) {
			throw new IllegalArgumentException(ERROR_RENTAL_DAYS);
		}
		return rentalDayCount;
	}

	private static double validateDiscountPercentage(int discountPercent) throws IllegalArgumentException {
		if (discountPercent < 0 || discountPercent > 100) {
			throw new IllegalArgumentException(ERROR_PERCENTAGE);
		}
		return ((double) discountPercent) / 100;
	}

	/*
	 * Calculator assistant methods
	 */
	private static int calculateChargeDays(ToolType toolType, RentalPeriod period) {
		int chargeDays = 0;
		if (toolType.hasWeekdayCharge() && period.getWeekdays() > 0) {
			chargeDays += period.getWeekdays();
		}
		if (toolType.hasWeekendCharge() && period.getWeekendDays() > 0) {
			chargeDays += period.getWeekendDays();
		}
		if (toolType.hasHolidayCharge() && period.getHolidays() > 0) {
			chargeDays += period.getHolidays();
		}
		return chargeDays;
	}

	/**
	 * Prints a rental agreement to the console
	 */
	public void printAgreement() {
		System.out.println();
		System.out.println(TOOL_CODE + getTool().getToolCode());
		System.out.println(TOOL_TYPE + getTool().getToolType());
		System.out.println(TOOL_BRAND + getTool().getBrand());
		System.out.println(RENTAL_DAYS + getRentalDays());
		System.out.println(CHECKOUT_DATE + formatDate(getCheckoutDate()));
		System.out.println(DUE_DATE + formatDate(getDueDate()));
		System.out.println(DAILY_CHARGE + formatCurrency(getToolType().getDailyCharge()));
		System.out.println(CHARGE_DAYS + getChargeDays());
		System.out.println(PRE_DISCOUNT_CHARGE + formatCurrency(getPreDiscountCharge()));
		System.out.println(DISCOUNT_PERCENT + formatPercent(getDiscountPercent()));
		System.out.println(DISCOUNT_AMOUNT + formatCurrency(getDiscountAmount()));
		System.out.println(FINAL_CHARGE + formatCurrency(getFinalCharge()));
		System.out.println();
	}

	private String formatCurrency(BigDecimal amount) {
		// Format for the configured locale
		NumberFormat formatter = NumberFormat.getCurrencyInstance(getAppConfig().getLocale());
		formatter.setCurrency(Currency.getInstance(getAppConfig().getLocale()));
		formatter.setGroupingUsed(true);
		return formatter.format(amount);
	}

	private String formatDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getAppConfig().getDateFormat());
		return formatter.format(date);
	}

	private String formatPercent(double percent) {
		return NumberFormat.getPercentInstance(getAppConfig().getLocale()).format(percent);
	}
}
