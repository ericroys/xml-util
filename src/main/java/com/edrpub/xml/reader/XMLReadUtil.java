package com.edrpub.xml.reader;

import java.util.List;

public class XMLReadUtil {

	private final XMLUtil reader;

	public XMLReadUtil(String confPath) throws XMLParserException {
		this.reader = new XMLUtil(confPath);
	}

	/**
	 * Get a text value of an element in xml or null if not found
	 * 
	 * @param xpath an xpath query string
	 * @return a string value found by the xpath statement
	 * @throws XMLParserException when inputs are invalid or there is an error processing the search
	 */
	public String getStringParameter(String xpath) throws XMLParserException {
		return reader.getParameterByXpath(xpath);
	}

	// get a parameter and if not found, throw an exception
	private String getRequired(String name, String xpathQuery) throws XMLParserException {
		String _tmp = getStringParameter(xpathQuery);
		if (isEmpty(_tmp)) {
			throw new XMLParserException("Parameter [" + name + "] is required but not found!");
		}
		return _tmp;
	}

	/**
	 * Get a string value for a parameter using xpath query. Throws
	 * an exception if not found.
	 * 
	 * @param name  friendly name of the parameter to find
	 * @param xpath an xpath query
	 * @return a string value for the xpath provided
	 * @throws XMLParserException when inputs are invalid or there is an error processing the search
	 */
	public String getRequiredString(String name, String xpath) throws XMLParserException {
		return getRequired(name, xpath);
	}

	/**
	 * checks if a string is null or empty
	 * 
	 * @param inString the string to test
	 * @return false if the string has a value otherwise false
	 */
	public boolean isEmpty(String inString) {
		return inString == null || inString == "";
	}

	/**
	 * Get an integer value for a given element or throw exception if not found
	 * or not an integer
	 * 
	 * @param name  a friendly name of the element to find
	 * @param xpath an xpath statement for the element to find
	 * @return an int value for the element found by the xpath provided
	 * @throws XMLParserException when inputs are invalid, the value is not an int, or error processing the search
	 */
	public int getRequiredInt(String name, String xpath) throws XMLParserException {
		try {
			String t = getRequired(name, xpath);
			return Integer.parseInt(t);
		} catch (Exception e) {
			throw new XMLParserException("Parameter [" + name + "] is invalid. Must be an integer");
		}
	}

	/**
	 * Get a boolean value
	 * 
	 * @param xpath an xpath statement for the element to find
	 * @return a boolean value for the element found by the xpath provided
	 * @throws XMLParserException when inputs are invalid or there is an error processing the search
	 */
	public boolean getBoolean(String xpath) throws XMLParserException {
		String t = getStringParameter(xpath);
		return !isEmpty(t) && t.equalsIgnoreCase("true") ? true : false;
	}

	/**
	 * Get a boolean value and if not found error
	 * 
	 * @param name a friendly name for the element to find
	 * @param xpath an xpath statement for the element to find
	 * @return	a boolean value for the element found by the xpath provided
	 * @throws XMLParserException when inputs are invalid or there is an error processing the search
	 */
	public boolean getRequiredBoolean(String name, String xpath) throws XMLParserException {
		String t = getRequiredString(name, xpath);
		return !isEmpty(t) && t.equalsIgnoreCase("true") ? true : false;
	}

	/**
	 * Get a list of string parameters matching a given xpath query
	 * 
	 * @param xpath an xpath statement for the element to find
	 * @return a List&lt;String&gt; of values found
	 * @throws XMLParserException when inputs are invalid or there is an error processing the search
	 */
	public List<String> getListParameters(String xpath) throws XMLParserException {
		return reader.getListParametersByXpath(xpath);
	}
}
