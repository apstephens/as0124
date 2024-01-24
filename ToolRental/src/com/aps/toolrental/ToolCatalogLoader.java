/**
 * 
 */
package com.aps.toolrental;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Properties;

/**
 * Class that implements the physical data loader for the ToolCatalog class.
 * This particular implementation reads its configuration from property files on
 * the classpath. It could be replaced with something that reads XML, JSON
 * documents, or even "hardcoded" static instance variables.
 * 
 * Property files were chosen because the data was "tabular" in nature, and
 * short (and simple). Since Java natively supports "properties", there are no
 * dependencies upon external libraries (ala "Jackson" for JSON, etc.)
 */
class ToolCatalogLoader {
	// Constant Reference Data
	private static final String TOOLS_PROPERTIES_FILE = "src/resources/tools.properties";
	private static final String TOOL_TYPES_PROPERTIES_FILE = "src/resources/tooltypes.properties";
	private static final String TOOL_CODES_PROP = "toolcodes";
	private static final String TOOL_TYPE_PROP = "tooltype";
	private static final String TOOL_BRAND_PROP = "brand";
	private static final String TOOLTYPE_TYPES_PROP = "tooltypes";
	private static final String TOOLTYPE_CHARGE_PROP = "dailyCharge";
	private static final String TOOLTYPE_WEEKDAY_PROP = "weekdayCharge";
	private static final String TOOLTYPE_WEEKEND_PROP = "weekendCharge";
	private static final String TOOLTYPE_HOLIDAY_PROP = "holidayCharge";

	private static final String ERROR_TOOLTYPE_NOT_FOUND = " does not exist in the tool type definitions: ";
	/**
	 * Reads the "tools.properties" file to populate the list of tool codes
	 * available to rent
	 * 
	 * @return Map of Tool object instances keyed by Tool Code
	 * @throws Exception if any of the Tool definitions are "malformed"
	 */
	static HashMap<String, Tool> initializeTools(HashMap<String, ToolType> toolTypes) throws Exception {
		// Load the properties file
		Properties toolProps = DataLoadPropertiesHelper.readConfigFile(TOOLS_PROPERTIES_FILE);

		// Find the list of tool codes defined
		String codeList = DataLoadPropertiesHelper.getCollectionKeyProperty(toolProps, TOOL_CODES_PROP,
				TOOLS_PROPERTIES_FILE);

		// OK, at least there's a property matching the property list. So now allocate
		// the storage for it
		HashMap<String, Tool> toolMap = new HashMap<String, Tool>();

		// Parse the list (comma separated values)
		String[] toolCodes = codeList.split(",");
		for (String code : toolCodes) {
			String toolCode = code.trim();
			// Find additional values
			String toolType = DataLoadPropertiesHelper.getChildProperty(toolProps, toolCode, TOOL_TYPE_PROP,
					TOOLS_PROPERTIES_FILE);
			// Verify that the tool type exists
			if (!toolTypes.containsKey(toolType)) {
				throw new Exception(toolCode + "." + TOOL_TYPE_PROP + ERROR_TOOLTYPE_NOT_FOUND 
						+ TOOLS_PROPERTIES_FILE);
			}
			String brand = DataLoadPropertiesHelper.getChildProperty(toolProps, toolCode, TOOL_BRAND_PROP,
					TOOLS_PROPERTIES_FILE);
			// Create new Tools object
			Tool tool = new Tool(toolCode, toolType, brand);
			// Add to the map
			toolMap.put(toolCode, tool);
		}
		return toolMap;
	}

	/**
	 * Reads the "tooltypes.properties" file to populate the tool types reference
	 * data
	 * 
	 * @return Map of ToolType object instances keyed by Tool Type name
	 * @throws Exception
	 */
	static HashMap<String, ToolType> initializeToolTypes() throws Exception {
		// Load the properties file
		Properties typeProps = DataLoadPropertiesHelper.readConfigFile(TOOL_TYPES_PROPERTIES_FILE);

		// Find the list of tool type codes defined
		String typeList = DataLoadPropertiesHelper.getCollectionKeyProperty(typeProps, TOOLTYPE_TYPES_PROP,
				TOOL_TYPES_PROPERTIES_FILE);

		HashMap<String, ToolType> toolMap = new HashMap<String, ToolType>();

		// Parse the list (comma separated values)
		String[] toolTypes = typeList.split(",");
		for (String typeName : toolTypes) {
			String toolTypeName = typeName.trim();
			// Find additional values
			String dailyChargeString = DataLoadPropertiesHelper.getChildProperty(typeProps, toolTypeName,
					TOOLTYPE_CHARGE_PROP, TOOL_TYPES_PROPERTIES_FILE);
			BigDecimal dailyCharge = DataLoadPropertiesHelper.convertDecimalString(dailyChargeString,
					toolTypeName + "." + TOOLTYPE_CHARGE_PROP, TOOL_TYPES_PROPERTIES_FILE);
			String weekdayChargeString = DataLoadPropertiesHelper.getChildProperty(typeProps, toolTypeName,
					TOOLTYPE_WEEKDAY_PROP, TOOL_TYPES_PROPERTIES_FILE);
			boolean weekdayCharge = DataLoadPropertiesHelper.convertBooleanString(weekdayChargeString,
					toolTypeName + "." + TOOLTYPE_WEEKDAY_PROP, TOOL_TYPES_PROPERTIES_FILE);
			String weekendChargeString = DataLoadPropertiesHelper.getChildProperty(typeProps, toolTypeName,
					TOOLTYPE_WEEKEND_PROP, TOOL_TYPES_PROPERTIES_FILE);
			boolean weekendCharge = DataLoadPropertiesHelper.convertBooleanString(weekendChargeString,
					toolTypeName + "." + TOOLTYPE_WEEKEND_PROP, TOOL_TYPES_PROPERTIES_FILE);
			String holidayChargeString = DataLoadPropertiesHelper.getChildProperty(typeProps, toolTypeName,
					TOOLTYPE_HOLIDAY_PROP, TOOL_TYPES_PROPERTIES_FILE);
			boolean holidayCharge = DataLoadPropertiesHelper.convertBooleanString(holidayChargeString,
					toolTypeName + "." + TOOLTYPE_HOLIDAY_PROP, TOOL_TYPES_PROPERTIES_FILE);

			// Create new ToolType object
			ToolType toolType = new ToolType(toolTypeName, dailyCharge, weekdayCharge, weekendCharge, holidayCharge);
			// Add to the map
			toolMap.put(toolTypeName, toolType);
		}
		return toolMap;
	}

}
