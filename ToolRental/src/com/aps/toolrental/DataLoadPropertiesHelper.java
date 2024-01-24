package com.aps.toolrental;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * Helper class to do basic file system manipulation to retrieve data load and
 * configuration properties files and to process the contents of them.
 */
class DataLoadPropertiesHelper {

	private static final String ERROR_FILE_NOT_FOUND = "Cannot find initialization file: ";
	private static final String ERROR_PROPERTIES_UNREADABLE = "Unable to parse initialization file: ";
	private static final String ERROR_PROPERTY_NOT_FOUND = " not found in: ";
	private static final String ERROR_INVALID_DOUBLE = " is not a valid 'double' number: ";
	private static final String ERROR_INVALID_BOOLEAN = " is not a valid 'boolean' value: ";
	private static final String ERROR_INVALID_INTEGER = " is not a valid 'integer' value: ";
	
	/**
	 * Helper Method to read an arbitrary configuration file for configuration
	 * and reference data loading.
	 * 
	 * @param filename
	 * @param mandatory
	 * @return Properties instance if file found and properly parsed, or null if
	 *         FileNotFound
	 * @throws Exception (wraps FileNotFound and IOExceptions)
	 */
	static Properties readConfigFile(String filename, boolean mandatory) throws Exception {
		File configFile = new File(filename);
		Properties props = new Properties();
		try {
			// Attempt to open the configuration file
			FileInputStream configReader = new FileInputStream(configFile);
			// Attempt to parse the tools configuration file
			try {
				props.load(configReader);
				configReader.close();
			} catch (IOException e) {
				// Couldn't parse properties file
				throw new Exception(ERROR_PROPERTIES_UNREADABLE + filename, e);
			}
		} catch (FileNotFoundException e) {
			// Couldn't find properties file in the classpath
			if (mandatory) {
				System.out.println(e.getLocalizedMessage());
				throw new Exception(ERROR_FILE_NOT_FOUND + filename, e);
			} else {
				return null;
			}
		}
		return props;
	}

	/**
	 * Overload of base method to be used if the configuration properties file must
	 * exist
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	static Properties readConfigFile(String filename) throws Exception {
		return readConfigFile(filename, true);
	}

	/**
	 * Helper method to extract a property that is a list (collection) of property
	 * names. This list's entries serve as the "key" to the remainder of the
	 * properties in the file
	 * 
	 * @param props
	 * @param propName
	 * @param propFileName
	 * @return
	 * @throws Exception
	 */
	static String getCollectionKeyProperty(Properties props, String propName, String propFileName)
			throws Exception {
		String collectionProp = props.getProperty(propName);
		if (collectionProp == null) {
			throw new Exception(propName + ERROR_PROPERTY_NOT_FOUND + propFileName);
		}
		return collectionProp;
	}

	/**
	 * Helper method to extract child configuration property values. Throws an
	 * exception if the required property is missing from the Properties file
	 * 
	 * @param props
	 * @param parentPropName
	 * @param childPropName
	 * @param propertyFileName
	 * @return returns a string with the child property value
	 * @throws Exception
	 */
	static String getChildProperty(Properties props, String parentPropName, String childPropName,
			String propFileName) throws Exception {
		String propName = parentPropName + "." + childPropName;
		String childProp = props.getProperty(propName);
		if (childProp == null) {
			throw new Exception(propName + ERROR_PROPERTY_NOT_FOUND + propFileName);
		}
		return childProp;
	}

	/**
	 * Helper method to convert a string property to its decimal representation
	 * 
	 * @param number
	 * @param propName
	 * @param propFileName
	 * @return returns a 'BigDecimal' representation as expressed by the input
	 *         string
	 * @throws IllegalArgumentException
	 */
	static BigDecimal convertDecimalString(String number, String propName, String propFileName)
			throws IllegalArgumentException {
		AppConfig config = AppConfig.getInstance();
		BigDecimal decimalVal;
		try {
			decimalVal = new BigDecimal(number);
			decimalVal.setScale(config.getScale(), config.getRoundingMode());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(propName + ERROR_INVALID_DOUBLE + propFileName, e);
		}
		return decimalVal;
	}

	/**
	 * Helper method to convert a string property to its boolean value
	 * 
	 * @param booleanStr
	 * @param propName
	 * @param propFileName
	 * @return returns a the 'boolean' intrinsic as expressed by the input string
	 * @throws IllegalArgumentException
	 */
	static boolean convertBooleanString(String booleanStr, String propName, String propFileName)
			throws IllegalArgumentException {
		boolean booleanVal = Boolean.parseBoolean(booleanStr);
		if (!booleanVal && !booleanStr.equalsIgnoreCase("false")) {
			throw new IllegalArgumentException(propName + ERROR_INVALID_BOOLEAN + propFileName);
		}
		return booleanVal;
	}

	/**
	 * Helper method to convert a string property to its integer representation
	 * 
	 * @param number
	 * @param propName
	 * @param propFileName
	 * @return returns a the 'double' intrinsic as expressed by the input string
	 * @throws IllegalArgumentException
	 */
	static int convertIntegerString(String number, String propName, String propFileName)
			throws IllegalArgumentException {
		int intVal;
		try {
			intVal = Integer.valueOf(number);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(propName + ERROR_INVALID_INTEGER + propFileName, e);
		}
		return intVal;
	}
}
