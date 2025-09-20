package com.edrpub.xml.reader;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.edr.test.TestNamespace;

@TestInstance(value = Lifecycle.PER_CLASS)
public abstract class XMLTest {
    private static final String BASE_PATH = "src/test/resources/data/";
    protected static final String FILENONS = BASE_PATH + "nonamespace.xml";
    protected static final String FILEBOOKS = BASE_PATH + "books.xml";
    protected static final String FILENS = BASE_PATH + "namespace.xml";
    protected static final String FILE_JOBREF = BASE_PATH + "findJobRefsResponse.xml";
    protected TestNamespace ns;

    public XMLTest() {
        this.ns = new TestNamespace();
    }
}
