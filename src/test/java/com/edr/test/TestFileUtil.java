package com.edr.test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPMessage;


public class TestFileUtil {
    private static final String PATH_BASE = "src/test/resources/data/";

    public static String getMessage(final String path) throws Exception {
		final MessageFactory mf = MessageFactory.newInstance();
		final Path p = Paths.get(PATH_BASE + path);
		
		final SOAPMessage msg = mf.createMessage(new MimeHeaders(), 
				new ByteArrayInputStream(
					Files.readAllBytes(p)));
		
		return messageToString(msg);
    }
        
    private static String messageToString(final SOAPMessage rtnMsg) throws Exception {
    	
		ByteArrayOutputStream bout = null;
		try{
			bout = new ByteArrayOutputStream(); 
			rtnMsg.writeTo(bout);
			return bout.toString("UTF-8");
		}catch(final Exception e) {
			throw new Exception("Unable to get xml from the soap response message", e);
		}
		finally {
			if(bout != null)
				try {
					bout.close();
				} catch (final IOException e) {}
		}
    }
}
