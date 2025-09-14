package com.edr.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;

import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;

public class TestNamespace implements NamespaceContext{
	private final Map<String, String> spaces = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
		    put("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
		    put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		    put("xsi","http://www.w3.org/2001/XMLSchema-instance");
		    put("xsd", "http://www.w3.org/2001/XMLSchema");
		    put("sear", "http://search.opsware.com");
		    put("job", "http://job.opsware.com");
		    put("ser", "http://server.opsware.com");
			put("ns1", "http://server.opsware.com");
		}};

	
	public void addNamespaceDef(final String key, final String value) {
		if(key != null && !key.isEmpty() && value != null && !value.isEmpty())
			spaces.put(key, value);
	}
	
	public SOAPEnvelope addNamespaces(final SOAPEnvelope env) throws SOAPException{
		if(env != null){
			for( final Entry<String, String> sp:spaces.entrySet())
				env.addNamespaceDeclaration(sp.getKey(), sp.getValue());
		}
		return env;
	}

	@Override
	public String getPrefix(final String arg0) {
		return null;
	}
	
	@Override
	public Iterator<String> getPrefixes(final String arg0) {
		return null;
	}

	@Override
	public String getNamespaceURI(final String key) {
		return spaces.get(key);
	}
}
