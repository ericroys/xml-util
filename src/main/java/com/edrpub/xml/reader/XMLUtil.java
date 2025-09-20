package com.edrpub.xml.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtil {
	private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);
	private static final String XPATH_MSG = "Invalid Xpath";
	private static final String XPATH_REQ = "An xpath qual is required!";
	private static final String CONTEXT_REQ = "A namespace context is required!";
	private static final String FILE_REQ = "A file/path is required!";
	private static final String TAG_REQ = "A tag is required";
	private static final String NS_REQ = "A namespace string is required";
	private final Document doc;
	private final String fileName;

	/**
	 * Instantiate XMLUtil using a path to an xml document
	 * 
	 * @param url full path to an xml file
	 * @throws XMLParserException if the input is invalid, there is an error opening
	 *                            the file, or
	 *                            there is something wrong with the xml document
	 *                            that doesn't allow it to be parsed.
	 */
	public XMLUtil(final String url) throws XMLParserException {
		validateRequired(url, FILE_REQ);
		this.doc = getDocument(url);
		this.fileName = url;
	}

	/**
	 * Instantiate XMLUtil using an existing DOM
	 * Handy of things like parsing DOM from a SOAP message.
	 * 
	 * @param document Document object
	 * @throws XMLParserException if the input is invalid
	 */
	public XMLUtil(final Document document) throws XMLParserException {
		if (document == null)
			throw new XMLParserException("A document is required!");
		this.doc = document;
		this.fileName = null;
	}

	private NodeList getNodes(final String qual) throws XMLParserException {
		validateRequired(qual, XPATH_REQ);
		try {
			final Object result = getExpression(qual).evaluate(doc, XPathConstants.NODESET);
			return (NodeList) result;
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	private NodeList getNodesNS(final String qual, final NamespaceContext context) throws XMLParserException {
		validateRequired(qual, XPATH_REQ);
		if (context == null)
			throw new XMLParserException(CONTEXT_REQ);
		try {
			final XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(context);
			final XPathExpression expr = xpath.compile(qual);
			return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	private Node getNode(final String qual) throws XMLParserException {
		validateRequired(qual, XPATH_REQ);
		try {
			return (Node) getExpression(qual).evaluate(doc, XPathConstants.NODE);
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	private Node getNodeNS(final String qual, final NamespaceContext context) throws XMLParserException {
		validateRequired(qual, XPATH_REQ);
		if (context == null)
			throw new XMLParserException(CONTEXT_REQ);
		try {
			final XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(context);
			final XPathExpression expr = xpath.compile(qual);
			return (Node) expr.evaluate(doc, XPathConstants.NODE);
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	/**
	 * Returns a string list of all the matching nodes specified by the qual input
	 * parameter.
	 * 
	 * @param qual String representation of an xpath query statement
	 * @return List&lt;String&gt; of values matchin the xpath
	 * @throws XMLParserException when the xpath is invalid or there is an error
	 *                            processing the search
	 */
	public List<String> getListParametersByXpath(final String qual) throws XMLParserException {
		final List<String> s = new ArrayList<String>();
		final NodeList nodes = getNodes(qual);
		for (int i = 0; i < nodes.getLength(); i++) {
			String txt = null;
			if ((txt = nodes.item(i).getTextContent()) != null) {
				s.add(txt);
			}
		}
		return s;
	}

	/**
	 * Returns a string list of all the matching nodes specified by the qual input
	 * parameter.
	 * 
	 * @param qual    String representation of an xpath query statement
	 * @param context Namespace context to include in the search
	 * @return List&lt;String&gt; of parameters matching the xpath
	 * @throws XMLParserException when the inputs are invalid or there is an error
	 *                            processing the search
	 */
	public List<String> getListParametersByXpathNS(final String qual, final NamespaceContext context)
			throws XMLParserException {
		final List<String> s = new ArrayList<String>();
		final NodeList nodes = getNodesNS(qual, context);
		if (nodes.getLength() < 1)
			return s;
		for (int i = 0; i < nodes.getLength(); i++) {
			String text = null;
			if ((text = nodes.item(i).getTextContent()) != null)
				s.add(text);
		}
		return s;
	}

	/**
	 * Returns a NodeList of all the matching nodes specified by the qual input
	 * parameter.
	 * 
	 * @param qual String representation of an xpath query statement
	 * @return org.w3c.dom.NodeList
	 * @throws XMLParserException when the input is invalid or there is an error
	 *                            processing the search
	 */
	public NodeList getNodeListByXpath(final String qual) throws XMLParserException {
		return getNodes(qual);
	}

	/**
	 * Returns a string value of the item found using the xpath in the qual input
	 * parameter
	 * 
	 * @param qual an xpath query string
	 * @return a String value of the element found with xpath
	 * @throws XMLParserException when the input is invalid or there is an error
	 *                            processing the search
	 */
	public String getParameterByXpath(final String qual) throws XMLParserException {
		final Node n = getNode(qual);
		return n != null ? n.getTextContent() : null;
	}

	/**
	 * Returns a string value of the item found using the xpath qual parameter
	 * and in the Namespace provided in the context parameter
	 * 
	 * @param qual    an xpath qualification string
	 * @param context a NamespaceContext
	 * @return string of found value or null
	 * @throws XMLParserException when the input is invalid or there is an error
	 *                            processing the search
	 */
	public String getParameterByXpathNS(final String qual, final NamespaceContext context) throws XMLParserException {
		final Node n = getNodeNS(qual, context);
		return n != null ? n.getTextContent() : null;
	}

	/**
	 * Returns a NodeList for matching items based on the the inputs of xpath
	 * statement and a NamespaceContext
	 * 
	 * @param xpath   an xpath statement
	 * @param context a NamespaceContext
	 * @return NodeList
	 * @throws XMLParserException when the input is invalid or there is an error
	 *                            processing the search
	 */
	public NodeList getNodeListByXpathNS(final String xpath, final NamespaceContext context) throws XMLParserException {
		return getNodesNS(xpath, context);
	}

	/**
	 * Get a Nodelist of results of a Namespace backed xpath query of the item
	 * object
	 * 
	 * @param qual    an xpath query string
	 * @param item    org.w3c.dom.Node in which to search with xpath
	 * @param context a NamespaceContext that is valid for the document
	 * @return org.w3c.dom.NodeList
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public NodeList getNodeListFromNodeByXpathNS(final String qual, final Node item, final NamespaceContext context)
			throws XMLParserException {
		try {
			return (NodeList) getExpression(qual, context).evaluate(item, XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	// construct and xpath expression from an xpath string and namespace
	private XPathExpression getExpression(final String qual, final NamespaceContext context)
			throws XPathExpressionException {
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(context);
		return xpath.compile(qual);
	}

	/**
	 * Returns a string list of all the matching nodes specified by the qual input
	 * parameter.
	 * 
	 * @param qual  String representation of an xpath query statement
	 * @param fName The document path/name to search
	 * @return List&lt;String&gt; of values found for the xpath statement
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static List<String> getListParametersByXpath(final String qual, final String fName)
			throws XMLParserException {
		try {
			final List<String> s = new ArrayList<String>();
			final NodeList nodes = (NodeList) getExpression(qual)
					.evaluate(getDocument(fName), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				String txt = null;
				if ((txt = nodes.item(i).getTextContent()) != null) {
					s.add(txt);
				}
			}
			return s;
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	private static void validateRequired(String value, String msg) throws XMLParserException {
		if (value == null || value == "")
			throw new XMLParserException(msg);
	}

	/**
	 * Returns a NodeList of all the matching nodes specified by the qual input
	 * parameter.
	 * 
	 * @param qual  String representation of an xpath query statement
	 * @param fName The document path/name to search
	 * @return org.w3c.dom.NodeList
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static NodeList getNodeListByXpath(final String qual, final String fName) throws XMLParserException {
		try {
			return (NodeList) getExpression(qual).evaluate(getDocument(fName), XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	/**
	 * Get a NodeList result from an xpath search of an item object
	 * 
	 * @param qual an xpath query string
	 * @param item an xml object
	 * @return org.w3c.dom.NodeList
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static NodeList getNodeListFromNodeByXpath(final String qual, final Node item)
			throws XMLParserException {
		validateRequired(qual, XPATH_REQ);
		if (item == null)
			throw new XMLParserException("An XML object is required!");
		try {
			return (NodeList) getExpression(qual).evaluate(item, XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	/**
	 * Get a string value from an xml file given an xpath query and file name
	 * 
	 * @param qual  an xpath statement
	 * @param fName a file name
	 * @return String
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static String getParameterByXpath(final String qual, final String fName) throws XMLParserException {
		try {
			final Node result = (Node) getExpression(qual).evaluate(getDocument(fName), XPathConstants.NODE);
			return result != null ? result.getTextContent() : null;
		} catch (final XPathExpressionException e) {
			throw new XMLParserException(XPATH_MSG, e);
		}
	}

	// Construct an XPathExpression using just an xpath string
	private static XPathExpression getExpression(final String qual) throws XPathExpressionException {
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		return xpath.compile(qual);
	}

	/**
	 * Returns the value of the node found using the specified tag.
	 * Used for 1 and only 1 key:value pair. If more than one node exists
	 * with the same tag it will return the first node in the hierarchy
	 * 
	 * @param tag   the tag of the xml element node to return.
	 * @param fName the file to parse
	 * @return the node value of a single element returned as a String.
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static String getParameterByTag(final String tag, final String fName) throws XMLParserException {
		final NodeList nl = getDocument(fName).getElementsByTagName(tag);
		if (nl.item(0) != null && nl.item(0).getFirstChild() != null)
			return nl.item(0).getFirstChild().getNodeValue();
		return null;
	}

	/**
	 * Returns the node(s) found using the specified tag.
	 * Used for more than 1 key:value pairs.
	 * 
	 * @param tag   the tag of the xml element node(s) to return.
	 * @param fName the file to parse
	 * @return org.w3c.dom.NodeList - elements of the node returned given the tag input or
	 *         null.
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static NodeList getNodeListByTag(final String tag, final String fName) throws XMLParserException {
		NodeList nl = getDocument(fName).getElementsByTagName(tag);
		if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
			return nl.item(0).getChildNodes();
		}
		return null;
	}

	/**
	 * Returns the List of String values found using the specified tag.
	 * Used for more than 1 key:value pairs.
	 * 
	 * @param tag   the tag of the xml element node(s) to return.
	 * @param fName the file to parse
	 * @return List&lt;String&gt;
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static List<String> getListParametersByTag(final String tag, final String fName)
			throws XMLParserException {
		List<String> ls = new ArrayList<String>();
		final NodeList nl = getDocument(fName).getElementsByTagName(tag);
		if (nl.getLength() > 0) {
			String tmp = null;
			for (int i = 0; i < nl.getLength(); i++) {
				NamedNodeMap atts = nl.item(i).getAttributes();
				tmp = atts.getLength() > 0 ? atts.item(0).getNodeValue() : null;
				if (tmp != null) {
					ls.add(tmp);
				}
			}
		}
		return ls;
	}

	/**
	 * Returns the List of String values found using the specified tag with
	 * namespace.
	 * Used for more than 1 key:value pairs.
	 * 
	 * @param tag       the tag of the xml element node(s) to return.
	 * @param namespace the namespace of the tag tag
	 * @return List&lt;String&gt;
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public List<String> getparametersByTagNS(final String tag, final String namespace) throws XMLParserException {
		validateRequired(tag, TAG_REQ);
		validateRequired(namespace, NS_REQ);
		List<String> ls = new ArrayList<String>();
		final NodeList nl = doc.getElementsByTagNameNS(namespace, tag);
		if (nl.getLength() > 0) {
			ls = new ArrayList<String>();
			String tmp = null;
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i).hasChildNodes()) {
					tmp = nl.item(i).getFirstChild().getNodeValue();
					if (tmp != null) {
						ls.add(tmp);
						log.trace("getParametersByTagNS():: Adding to List [" + tmp + "]");
					}
				}
			}
		}
		return ls;
	}

	/**
	 * Returns the value of the node found using the specified tag and namespace.
	 * Used for 1 and only 1 key:value pair. If more than one node exists
	 * with the same tag and namespace it will return the first node in the
	 * hierarchy
	 * 
	 * @param tag       the tag of the xml element node to return.
	 * @param namespace the namespace uri of the tag
	 * @return the node value of a single element returned as a String
	 *         or null if not found.
	 */
	public String getParameterByTagNS(final String tag, final String namespace) {
		String x = null;
		final NodeList nl = doc.getElementsByTagNameNS(namespace, tag);
		if (nl != null && nl.getLength() > 0 && nl.item(0) != null
				&& nl.item(0).hasChildNodes()) {
			x = nl.item(0).getFirstChild().getNodeValue();
		}
		return x;
	}

	/**
	 * Returns a map of key value pairs found using the specified tag.
	 * Used for more than 1 key:value pairs. Provides for a unique key
	 * with multiple values per key.
	 * 
	 * @param tag   the tag of the xml element node(s) to return.
	 * @param fName the file to parse
	 * @return map - elements of the node name/value returned given the tag input.
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static Map<String, List<String>> getMapByTag(final String tag, final String fName)
			throws XMLParserException {
		validateRequired(tag, TAG_REQ);
		validateRequired(fName, FILE_REQ);
		final Map<String, List<String>> map = new HashMap<String, List<String>>();
		final NodeList nl = getNodeListByTag(tag, fName);
		String node2 = null;
		List<String> temp = null;
		for (int i = 0; i < nl.getLength(); i++) {
			while (nl.item(i).hasChildNodes()) {
				if (nl.item(i).getFirstChild().getNodeType() == 1) {
					while (nl.item(i).getFirstChild().hasChildNodes()) {
						if (!nl.item(i).getFirstChild().getNodeName().equals(node2)) {
							node2 = nl.item(i).getFirstChild().getNodeName();
							temp = new ArrayList<String>();
						}
						temp.add(nl.item(i).getFirstChild().getFirstChild().getNodeValue());
						nl.item(i).getFirstChild().removeChild(nl.item(i).getFirstChild().getFirstChild());
					}
					map.put(node2, temp);
				}
				nl.item(i).removeChild(nl.item(i).getFirstChild());
			}
		}
		return map;
	}

	/**
	 * Returns a map of key value pairs found using the xpath string.
	 * Used for more than 1 key:value pairs. Provides for a unique key
	 * with multiple values per key.
	 * 
	 * @param qual  the tag of the xml element node(s) to return.
	 * @param fName the file to parse
	 * @return map - elements of the node name/value returned given the tag input.
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the search
	 */
	public static Map<String, List<String>> getMapByXpath(final String qual, final String fName)
			throws XMLParserException {
		validateRequired(qual, XPATH_REQ);
		validateRequired(fName, FILE_REQ);
		final Map<String, List<String>> map = new HashMap<String, List<String>>();
		NodeList nl;
		Object result;
		try {
			result = getExpression(qual).evaluate(getDocument(fName), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new XMLParserException("Invalid xpath [" + qual + "]", e);
		}
		nl = (NodeList) result;
		String node2 = null;
		List<String> temp = null;
		for (int i = 0; i < nl.getLength(); i++) {
			while (nl.item(i).hasChildNodes()) {
				if (nl.item(i).getFirstChild().getNodeType() == 1) {
					while (nl.item(i).getFirstChild().hasChildNodes()) {
						if (!nl.item(i).getFirstChild().getNodeName().equals(node2)) {
							node2 = nl.item(i).getFirstChild().getNodeName();
							temp = new ArrayList<String>();
						}
						temp.add(nl.item(i).getFirstChild().getFirstChild().getNodeValue());
						nl.item(i).getFirstChild().removeChild(nl.item(i).getFirstChild().getFirstChild());
					}
					map.put(node2, temp);
				}
				nl.item(i).removeChild(nl.item(i).getFirstChild());
			}
		}
		return map;
	}

	/**
	 * Returns the DOM of the xml file so that elements can be
	 * extracted.
	 * 
	 * @param fileName get a Document object from a filename
	 * @return DOM (Document Object Model) of the xml file
	 * @throws XMLParserException when there is a problem parsing the file into DOM
	 */
	private static Document getDocument(final String fileName) throws XMLParserException {
		validateRequired(fileName, FILE_REQ);
		try {
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setNamespaceAware(true);
			dbf.setIgnoringElementContentWhitespace(true);
			final DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(fileName);

		} catch (final ParserConfigurationException e) {
			log.error("Error parsing file");
			throw new XMLParserException("FATAL: Invalid parser configuration", e);
		} catch (final SAXException e) {
			log.error("Sax parse error.");
			throw new XMLParserException("Sax parse error", e);
		} catch (final IOException e) {
			log.error("Unable to find/read file: " + fileName);
			throw new XMLParserException("Unable to find/read file: " + fileName, e);
		}
	}

	/**
	 * Updates an existing node with a new value in the file
	 * or adds it if not already present
	 * 
	 * @param xpath the name of the node to modify
	 * @param value the value to set for the node value
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the add/update
	 */
	public void updateOrAddElement(final String xpath, final String value) throws XMLParserException {
		validateRequired(xpath, XPATH_REQ);
		final Node n = getNode(xpath);
		if (n != null) {
			// update existing node
			n.setTextContent(value);
			updateDocument();
		} else {
			// add new element
			final String parent = xpath.substring(0, xpath.lastIndexOf("/"));
			final String nodeName = xpath.substring(xpath.lastIndexOf("/") + 1, xpath.length());
			addElement(parent, nodeName, value);
			updateDocument();
		}
	}

	/**
	 * Remove a node from the existing document
	 * 
	 * @param xpath an xpath statement to identify the node to remove
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the deletion
	 */
	public void removeNode(final String xpath) throws XMLParserException {
		final Node n = getNode(xpath);
		if (n != null) {
			Node parent = n.getParentNode();
			parent.removeChild(n);
			updateDocument();
		}
	}

	/**
	 * Adds an element with a text node (value)
	 * 
	 * @param xpath the xpath for the parent node where node to be added
	 * @param name  the name of the new node
	 * @param value the value to add (not required)
	 * @return Node
	 * @throws XMLParserException when inputs are invalid or there is an error
	 *                            processing the request
	 */
	private Node addElement(final String xpath, final String name, final String value) throws XMLParserException {
		validateRequired(xpath, XPATH_REQ);
		validateRequired(name, "A node name is required");
		validateRequired(value, "A value is required");
		Node n = getNode(xpath);
		if (n != null) {
			n = n.appendChild(doc.createElement(name));
			n.appendChild(doc.createTextNode(value));
		} else {
			log.warn("addElement() parent not found at [" + xpath + "]");
		}
		return n;
	}

	/**
	 * Updates the xml configuration file DOM with an updated DOM.
	 * 
	 * @param Document doc
	 * @return void
	 * @throws XMLParserException
	 */
	private void updateDocument() throws XMLParserException {
		validateRequired(fileName, FILE_REQ);
		try {
			final Source source = new DOMSource(doc);
			final StreamResult result = new StreamResult(fileName);
			final Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (final TransformerConfigurationException e) {
			log.error("transformer configuration error while writing to file: " + e);
			throw new XMLParserException("transformer configuration error while writing to file", e);
		} catch (final TransformerException e) {
			log.error("transformer error while writing to file", e);
			throw new XMLParserException("transformer error while writing to file", e);
		}
	}
}
