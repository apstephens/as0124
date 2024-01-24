package com.aps.toolrental;

import java.util.HashMap;

/**
 * Class that holds the tool data for the application:
 * 
 * - Tools: the collection of tools for rent, keyed by toolCode
 * - ToolTypes: the collection of types of tools, along with their prices
 *   and rental day rules.
 * 
 * It is the access point for the rest tool rental application to retrieve
 * details about tools and tool types.
 * 
 * This is a singleton class (there need be only one catalog instance). It uses
 * lazy initialization.
 * 
 */

class ToolCatalog {
	// Initialize the catalog
	private static ToolCatalog instance = null;
	private HashMap<String, Tool> tools = null;
	private HashMap<String, ToolType> toolTypes = null;
	
	// Error Messages
	private static final String ERROR_CANT_INITIALIZE = "Unable to initialize " + ToolCatalog.class.getName() 
			+ ". Cause:";

	/**
	 * Private Constructor for initialization
	 */
	private ToolCatalog() {
			try {
				setToolTypes(ToolCatalogLoader.initializeToolTypes());
				setTools(ToolCatalogLoader.initializeTools(getToolTypes()));
			} catch (Exception e) {
				System.out.println(ERROR_CANT_INITIALIZE);
				System.out.println(e.getMessage());
				throw new RuntimeException(e);
			}
	}

	// Private methods
	private void setTools(HashMap<String, Tool> tools) {
		this.tools = tools;
	}

	private HashMap<String, Tool> getTools() {
		return tools;
	}

	private void setToolTypes(HashMap<String, ToolType> toolTypes) {
		this.toolTypes = toolTypes;
	}

	private HashMap<String, ToolType> getToolTypes() {
		return toolTypes;
	}

	// Default (protected) Methods
	/**
	 * Gets a reference to the initialized ToolCatalog instance
	 * 
	 * @return instance
	 */
	static ToolCatalog getInstance() {
		if (instance == null) {
			instance = new ToolCatalog();
		}
		return instance;
	}

	/**
	 * Returns a Tool entity, referenced by its tool code
	 * 
	 * @param toolCode
	 * @return
	 */
	Tool getTool(String toolCode) {
		return getTools().get(toolCode);
	}

	/**
	 * Returns a ToolType entity, referenced by its tool type "name"
	 * 
	 * @param toolType
	 * @return
	 */
	ToolType getToolType(String toolType) {
		return getToolTypes().get(toolType);
	}


}