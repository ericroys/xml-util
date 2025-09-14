package com.edrpub.xml.reader;

public class XMLParserException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public XMLParserException(String message){
        super(message);
    }

    public XMLParserException(String message, Throwable t){
        super(message, t);
    }

}
