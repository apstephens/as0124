package com.aps.toolrental;

/**
 * Simple POJO to model Tool definitions.  It is immutable after instantiation,
 * since it holds reference data, and is publicly accessible
 */
public class Tool {
	private final String toolCode;
	private final String toolType;
	private final String brand;

	/**
	 * Constructor
	 */
	public Tool(String toolCode, String toolType, String brand) {
		this.toolCode = toolCode;
		this.toolType = toolType;
		this.brand = brand;
	}

	/*
	 * Accessor methods
	 */
	public String getToolCode() {
		return toolCode;
	}

	public String getToolType() {
		return toolType;
	}

	public String getBrand() {
		return brand;
	}
}
