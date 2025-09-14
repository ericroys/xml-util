package com.edrpub.xml.reader;

import java.text.SimpleDateFormat;
import java.util.List;

public class XMLReadUtil {

	private final XMLUtil reader;

	public XMLReadUtil(String confPath) throws XMLParserException {
		this.reader = new XMLUtil(confPath);
	}

	/**
	 * Get a text value of an element in xml or null if not found
	 * 
	 * @param param
	 * @return
	 * @throws XMLParserException
	 */
	public String getParam(String param) throws XMLParserException {
		return reader.getParameterByXpath(param);
	}

	// get a parameter and if not found, throw an exception
	private String getRequired(String name, String xpathQuery) throws XMLParserException {
		if (isEmpty(name) || isEmpty(xpathQuery))
			throw new IllegalArgumentException("name and xpathQuery are required");
		String _tmp = getParam(xpathQuery);
		if (isEmpty(_tmp)) {
			throw new IllegalArgumentException("Parameter [" + name + "] is required but not found!");
		}
		return _tmp;
	}

	/**
	 * Get a string value for a parameter using xpath query. Throws
	 * an exception if not found.
	 * 
	 * @param name       friendly name of the parameter to find
	 * @param xpathQuery an xpath query
	 * @return
	 * @throws XMLParserException
	 */
	public String getRequiredStr(String name, String xpathQuery) throws XMLParserException {
		if (isEmpty(name) || isEmpty(xpathQuery))
			throw new IllegalArgumentException("name and xpathQuery are required");
		return getRequired(name, xpathQuery);
	}

	/**
	 * checks if a string is null or empty
	 * 
	 * @param inString
	 * @return
	 */
	public boolean isEmpty(String inString) {
		return inString == null || inString == "";
	}

	/**
	 * Get an integer value for a given element or throw exception if not found
	 * or not an integer
	 * 
	 * @param name       friendly name of the element to find
	 * @param xpathQuery
	 * @return
	 */
	public int getRequiredInt(String name, String xpathQuery) {
		if (isEmpty(name) || isEmpty(xpathQuery))
			throw new IllegalArgumentException("name and xpathQuery are required");
		try {
			String t = getRequired(name, xpathQuery);
			return Integer.parseInt(t);
		} catch (Exception e) {
			throw new IllegalArgumentException("Parameter [" + name + "] is invalid. Must be an integer");
		}
	}

	/**
	 * Get a boolean value
	 * 
	 * @param xpathQuery
	 * @throws XMLParserException
	 */
	public boolean getBoolean(String xpathQuery) throws XMLParserException {
		if (isEmpty(xpathQuery))
			throw new IllegalArgumentException("xpathQuery is required");
		String t = getParam(xpathQuery);
		return !isEmpty(t) && t.equalsIgnoreCase("true") ? true : false;
	}

	/**
	 * Get a boolean value and if not found error
	 * 
	 * @param name
	 * @param xpathQuery
	 * @return
	 * @throws XMLParserException
	 */
	public boolean getRequiredBoolean(String name, String xpathQuery) throws XMLParserException {
		if (isEmpty(name) || isEmpty(xpathQuery))
			throw new IllegalArgumentException("name and xpathQuery are required");
		String t = getRequiredStr(name, xpathQuery);
		return !isEmpty(t) && t.equalsIgnoreCase("true") ? true : false;
	}

	public SimpleDateFormat getDateFormat(String p, String s) {
		try {
			String t = getParam(s);
			return new SimpleDateFormat(t);
		} catch (Exception e) {
			throw new IllegalArgumentException("Parameter [" + p + "] is invalid. Must be a proper date format");
		}
	}

	/**
	 * Get a list of string parameters matching a given xpath query
	 * 
	 * @param xpathQuery
	 * @return
	 * @throws XMLParserException
	 */
	public List<String> getParams(String xpathQuery) throws XMLParserException {
		if (isEmpty(xpathQuery))
			throw new IllegalArgumentException("xpathQuery is required");
		return reader.getListParametersByXpath(xpathQuery);
	}
}
