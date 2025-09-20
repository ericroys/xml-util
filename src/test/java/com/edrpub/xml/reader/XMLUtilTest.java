package com.edrpub.xml.reader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.edr.test.TestNamespace;

public class XMLUtilTest extends XMLTest{

    @Test
    void testUpdateOrAddElement() throws XMLParserException {
        XMLUtil util = new XMLUtil(FILEBOOKS);
        String expectedName = "Needful Things";
        String expectedVersion = "1";
        // make sure name node is not there before test
        util.removeNode("//editableBook/name");
        util.updateOrAddElement("//editableBook/version", expectedVersion);
        util.updateOrAddElement("//editableBook/name", expectedName);
        assertAll(
                () -> assertEquals(expectedVersion, util.getParameterByXpath("//editableBook/version")),
                () -> assertEquals(expectedName, util.getParameterByXpath("//editableBook/name")));
        // cleanup/reset
        util.updateOrAddElement("//editableBook/version", "notSet");
    }

    @Test
    void testGetMapByTag() throws XMLParserException {
        List<String> expected = Arrays.asList(new String[] { "1", "2", "3", "4" });
        final Map<String, List<String>> value = XMLUtil.getMapByTag("mybook", FILEBOOKS);
        assertAll(
                () -> assertNotNull(value),
                () -> assertTrue(value.size() > 0),
                () -> assertTrue(value.keySet().contains("version")),
                () -> assertLinesMatch(expected, value.get("version")));
    }

    @Test
    void testCreateException() throws XMLParserException {
        Document d = null;
        final Document mockDoc = mock(Document.class);
        String s = null;
        String t = "";
        assertThrows(XMLParserException.class, () -> {
            new XMLUtil(d);
        });
        assertThrows(XMLParserException.class, () -> {
            new XMLUtil(s);
        });
        assertThrows(XMLParserException.class, () -> {
            new XMLUtil(t);
        });
        new XMLUtil(mockDoc);
    }

    @Test
    void testGetMapByXpath() throws XMLParserException {
        Map<String, List<String>> map = new HashMap<String, List<String>>() {
            {
                put("version", Arrays.asList(new String[] { "1", "2", "3", "4" }));
            }
        };
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getMapByXpath(
                    "--*709!",
                    FILEBOOKS);
        });
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getMapByXpath(
                    "//mybook/edition",
                    null);
        });
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getMapByXpath(
                    "//mybook/edition",
                    "");
        });
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getMapByXpath(
                    null,
                    FILEBOOKS);
        });
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getMapByXpath(
                    "",
                    FILEBOOKS);
        });
        Map<String, List<String>> value = XMLUtil.getMapByXpath(
                "//mybook/edition",
                FILEBOOKS);
        Map<String, List<String>> noValue = XMLUtil.getMapByXpath(
                "//mybook/editionX",
                FILEBOOKS);
        assertAll(
                "testGetMapByXpath",
                () -> assertNotNull(noValue, "Expected empty map for not found"),
                () -> assertTrue(noValue.isEmpty(), "Expected empty map"),
                () -> assertNotNull(value),
                () -> assertEquals(map.size(), value.size()));
        // validate key/values
        map.forEach((k, v) -> {
            // check key
            assertNotNull(value.get(k));
            // check lists values
            assertEquals(v, value.get(k));
        });
    }

    @Test
    void testGetNodeListByXpath() throws XMLParserException {
        validNodeList(new XMLUtil(FILEBOOKS).getNodeListByXpath("//mybook/edition"));
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getNodeListByXpath("n!3k", FILEBOOKS);
        });
    }

    @Test
    void testGetNodeListByXpathStatic() throws XMLParserException {
        validNodeList(XMLUtil.getNodeListByXpath("//mybook/edition", FILEBOOKS));
        assertThrows(XMLParserException.class, () -> {
            new XMLUtil(FILEBOOKS).getNodeListByXpath("n!ks");
        });
        assertThrows(XMLParserException.class, () -> {
            new XMLUtil(FILEBOOKS).getNodeListByXpath("n!ks");
        });
    }

    @Test
    void testGetNodeListFromNodByXpath() throws XMLParserException {
        final NodeList nl = new XMLUtil(FILEBOOKS).getNodeListByXpath("//bookstore/book[@category=\"WEB\"]");
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getNodeListFromNodeByXpath("//changeid", null);
        });
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getNodeListFromNodeByXpath("//n!dk", nl.item(0));
        });

        final NodeList value = XMLUtil.getNodeListFromNodeByXpath("//book/title", nl.item(0));
        assertAll(
                () -> assertTrue(value.getLength() > 0));
    }

    @Test
    void testGetNodeListByXpathNS() throws XMLParserException {
        XMLUtil util = new XMLUtil(FILE_JOBREF);
        assertThrows(XMLParserException.class, () -> {
            util.getNodeListByXpathNS("//findJobRefsReturn/item/@href", null);
        });
        assertThrows(XMLParserException.class, () -> {
            util.getNodeListByXpathNS("", ns);
        });
        NodeList nodes = util.getNodeListByXpathNS("//findJobRefsReturn/item/@href", ns);
        assertAll(
                "Invalid result from testGetNodeListByXpathNS",
                () -> assertNotNull(nodes, "node list is null"),
                () -> assertEquals(21, nodes.getLength(), "node list should contain 21 entries"));
    }

    @Test
    void testGetNodeListFromNodeByXpathNS() throws XMLParserException {
        XMLUtil util = new XMLUtil(FILE_JOBREF);
        NodeList nl = util.getNodeListByXpathNS("//multiRef[@id=\"id17\"]", ns);
        assertAll(
                "Setup for testGetNodeListFromNodeByXpathNS",
                () -> assertNotNull(nl, "Initial node should exist"),
                () -> assertEquals(1, nl.getLength(), "Initial list should have one"));
        NodeList nodes = util.getNodeListFromNodeByXpathNS("name", nl.item(0), ns);
        assertAll(
                "testGetNodeListFromNodeByXpathNS",
                () -> assertNotNull(nodes),
                () -> assertEquals(1, nodes.getLength(), "Expected one result"),
                () -> assertEquals(
                        "Automated Communications Test for core: 1",
                        nodes.item(0).getTextContent()));
        assertThrows(XMLParserException.class, () -> {
            util.getNodeListFromNodeByXpathNS("x!cfs", nl.item(0), ns);
        });
    }

    @Test
    void testGetParameterByTag() throws XMLParserException {
        String value = XMLUtil.getParameterByTag("userName", FILENONS);
        String noValue = XMLUtil.getParameterByTag("invalidTag", FILENONS);
        assertAll("getParameterByTag",
                () -> assertNotNull(value),
                () -> assertEquals("connect", value),
                () -> assertNull(noValue, "Expected this to be null for invalid tag"));
    }

    @Test
    void testGetParameterByTagNS() throws XMLParserException {
        XMLUtil util = new XMLUtil(FILENS);
        String value = util.getParameterByTagNS("ChangeID", "http://server.opsware.com");
        String noValue = util.getParameterByTagNS("ChangeIDx", "http://server.opsware.com");
        assertAll("GetParametersByTagNS",
                () -> assertNotNull(value),
                () -> assertEquals("CHG000001", value, "Output doesn't match"),
                () -> assertNull(noValue, "Expected null for not found"));
    }

    @Test
    void testGetParameterByXpath() throws XMLParserException {
        XMLUtil util = new XMLUtil(FILENONS);
        String value = util.getParameterByXpath("//main/asyncProcessingThreads");
        String noValue = util.getParameterByXpath("//main/asyncProcessingThreads1");
        assertAll(
                "GetParameterByXpath",
                () -> assertNotNull(value),
                () -> assertEquals("5", value, "Expected value of 5"),
                () -> assertNull(noValue, "Expected null for not found"));
        assertThrows(XMLParserException.class, () -> {
            util.getParameterByXpath("!948");
        });
    }

    @Test
    void testGetParameterByXpathStatic() throws XMLParserException {
        String value = XMLUtil.getParameterByXpath("//main/asyncProcessingThreads", FILENONS);
        String noValue = XMLUtil.getParameterByXpath("//main/asyncProcessingThreads1", FILENONS);
        assertAll(
                "GetParameterByXpath",
                () -> assertNotNull(value),
                () -> assertEquals("5", value, "Expected value of 5"),
                () -> assertNull(noValue, "Should have null for not found"));
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getParameterByXpath("//x!edf", FILENONS);
        });
    }

    @Test
    void testGetParameterByXpathNS() throws XMLParserException {
        XMLUtil util = new XMLUtil(FILENS);
        String value = util.getParameterByXpathNS("//ns1:ChangeID", ns);
        String noValue = util.getParameterByXpathNS("//ChangeIDx", ns);
        assertAll("GetParameterByTagNS",
                () -> assertNotNull(value),
                () -> assertEquals("CHG000001", value, "Output doesn't match"),
                () -> assertNull(noValue, "Expected null for not found"));
        assertThrows(XMLParserException.class, () -> {
            util.getParameterByXpathNS("//ChangeID", null);
        });
        assertThrows(XMLParserException.class, () -> {
            util.getParameterByXpathNS("//n1!cy", ns);
        });
    }

    @Test
    void testGetNodeListByTagStatic() throws XMLParserException {
        final NodeList value = XMLUtil.getNodeListByTag("maps", FILENONS);
        final NodeList noValue = XMLUtil.getNodeListByTag("notag", FILENONS);
        assertAll(
                () -> assertNotNull(value),
                () -> assertTrue(value.getLength() > 0),
                () -> assertNull(noValue, "Expected null for not found"));
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getNodeListByTag("x", null);
        });
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getNodeListByTag("x", "z:/tmp/notafile.xml");
        });
    }

    @Test
    void testGetListParametersByTag() throws XMLParserException {
        List<String> expected = Arrays.asList("Incident", "Change");
        List<String> value = XMLUtil.getListParametersByTag("map", FILENONS);
        List<String> noValue = XMLUtil.getListParametersByTag("unavailable", FILENONS);
        assertAll(
                "GetListParametersByTag",
                () -> assertNotNull(noValue, "No value expected to be empty list"),
                () -> assertTrue(noValue.isEmpty()),
                () -> assertNotNull(value, "Expected non null list for found result"),
                () -> assertLinesMatch(expected, value));
    }

    @Test
    void testGetListParametersByXpath() throws XMLParserException {
        List<String> expected = Arrays.asList("Incident", "Change");
        XMLUtil util = new XMLUtil(FILENONS);
        List<String> value = util.getListParametersByXpath("//maps/map/@name");
        List<String> noValue = util.getListParametersByXpath("//novalue");
        assertAll(
                "GetListParametersByXpath",
                () -> assertNotNull(noValue),
                () -> assertTrue(noValue.isEmpty()),
                () -> assertNotNull(value),
                () -> assertLinesMatch(expected, value));
        assertThrows(XMLParserException.class, () -> {
            util.getListParametersByXpath("!si94");
        });
    }

    @Test
    void testGetListParametersByXpathStatic() throws XMLParserException {
        final List<String> expected = Arrays.asList("Incident", "Change");
        final List<String> value = XMLUtil.getListParametersByXpath("//maps/map/@name", FILENONS);
        final List<String> noValue = XMLUtil.getListParametersByXpath("//novalue", FILENONS);
        assertAll(
                "GetListParametersByXpath",
                () -> assertNotNull(noValue),
                () -> assertTrue(noValue.isEmpty()),
                () -> assertNotNull(value),
                () -> assertLinesMatch(expected, value));
        assertThrows(XMLParserException.class, () -> {
            XMLUtil.getListParametersByXpath("!d4", FILENONS);
        });
    }

    @Test
    void testGetparametersByTagNS() throws XMLParserException {
        final List<String> expected = Arrays.asList("0", "1", "2");
        final XMLUtil util = new XMLUtil(FILENS);
        final List<String> value = util.getparametersByTagNS("Id", "http://server.opsware.com");
        final List<String> novalue = util.getparametersByTagNS("ns1:nothingd", "http://server.opsware.com");
        assertAll(
                () -> assertNotNull(value),
                () -> assertLinesMatch(expected, value),
                () -> assertNotNull(novalue),
                () -> assertTrue(novalue.isEmpty()));
        assertThrows(XMLParserException.class, () -> {
            util.getparametersByTagNS(null, "http://server.opsware.com");
        });
        assertThrows(XMLParserException.class, () -> {
            util.getparametersByTagNS("Id", null);
        });
    }

    @Test
    void testGetParametersByXpathNS() throws XMLParserException {
        final List<String> expected = Arrays.asList("id1", "id0");
        final XMLUtil util = new XMLUtil(FILENS);
        final List<String> value = util
                .getListParametersByXpathNS("//soapenv:Body/multiRef/@id", new TestNamespace());
        final List<String> noValue = util
                .getListParametersByXpathNS("notFoundXpath", new TestNamespace());
        assertAll(
                "GetListParametersByXpathNS",
                () -> assertNotNull(value),
                () -> assertLinesMatch(expected, value),
                () -> assertNotNull(noValue),
                () -> assertTrue(noValue.isEmpty()));
        assertThrows(XMLParserException.class, () -> {
            util.getListParametersByXpathNS("!d4", ns);
        });
    }

    // validate the nodelist response from getNodeListByXpath
    private void validNodeList(NodeList nl) {
        assertNotNull(nl);
        Map<String, List<String>> map = new HashMap<String, List<String>>() {
            {
                put("version", Arrays.asList(new String[] { "1", "2", "3", "4" }));
            }
        };

        // make sure response map has correct size
        assertEquals(map.get("version").size(), nl.getLength());
        // make sure response has all our parent/keys/values
        assertEquals(nl.item(0).getLocalName(), "edition");
        assertEquals(nl.item(0).getParentNode().getLocalName(), "mybook");

        map
                .get("version")
                .stream()
                .forEach(s -> {
                    boolean match = false;
                    for (int i = 0; i < nl.getLength(); i++) {
                        if (match = s.equals(nl.item(i).getTextContent().trim()))
                            break;
                    }
                    assertTrue(match, "Expected a match for " + s);
                });
    }
}