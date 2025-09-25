package com.edrpub.xml.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class XMLReadUtilTest extends XMLTest {

    @Test
    void testConstructor() throws XMLParserException {
        assertNotNull(new XMLReadUtil(FILEBOOKS));
        assertThrows(XMLParserException.class, () -> {
            new XMLReadUtil(null);
        });

        assertThrows(XMLParserException.class, () -> {
            new XMLReadUtil("unavailablePath");
        });
    }

    @Test
    void testGetBoolean() throws XMLParserException {
        final XMLReadUtil reader = new XMLReadUtil(FILENONS);
        assertFalse(reader.getBoolean("//subMap[@name=\"SUBTASK\"]/inline"));
        assertThrows(XMLParserException.class, () -> { 
            reader.getBoolean("!232d");
        });
        assertFalse(reader.getBoolean("//noresults/bob"));

    }

    @Test
    void testGetStringParam() throws XMLParserException {
        XMLReadUtil reader = new XMLReadUtil(FILENONS);
        assertEquals("pathtomapfile.map", reader
            .getStringParameter("//transformation/maps/map[@name=\"Incident\"]/path"));
    }

    @Test
    void testGetListParams() throws XMLParserException {
        final XMLReadUtil reader = new XMLReadUtil(FILENONS);
        List<String> expected = Arrays.asList("Incident", "Change");
        assertLinesMatch(expected, reader.getListParameters(
            "//transformation/maps/map/@name"
        ));
    }

    @Test
    void testGetRequiredBoolean() throws XMLParserException {
        final XMLReadUtil reader = new XMLReadUtil(FILENONS);
        assertFalse(reader.getRequiredBoolean("inline", "//subMap[@name=\"SUBTASK\"]/inline"));
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredBoolean("inline","!232d");
        });
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredBoolean("inline", "//noresults/bob");
        });
    }

    @Test
    void testGetRequiredInt() throws XMLParserException {
        final XMLReadUtil reader = new XMLReadUtil(FILENONS);
        assertEquals(5, reader.getRequiredInt("threads", "//asyncProcessingThreads"));
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredInt("threads","!232d");
        });
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredInt("threads", "//noresults/bob");
        });
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredInt("NAN", "//serviceUrl");
        });
    }

    @Test
    void testGetRequiredStr() throws XMLParserException {
        final XMLReadUtil reader = new XMLReadUtil(FILENONS);
        assertEquals(5, reader.getRequiredInt("threads", "//asyncProcessingThreads"));
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredInt("threads","!232d");
        });
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredInt("threads", "//noresults/bob");
        });
        assertThrows(XMLParserException.class, () -> { 
            reader.getRequiredInt("NAN", "//serviceUrl");
        });
    }

    @Test
    void testIsEmpty() throws XMLParserException {
        final XMLReadUtil reader = new XMLReadUtil(FILENONS);
        assertFalse(reader.isEmpty("astring"));
        assertTrue(reader.isEmpty(null));
    }
}
