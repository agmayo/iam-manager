package org.acme.iam.manager.exceptions;

import java.io.IOException;

/**
 * BasicAuthHeaderParsingException
 */
public class BasicAuthHeaderParsingException extends IOException {
    
    /**
	 * Generated serial version number.
	 */
	private static final long serialVersionUID = 8182856979082464470L;

	public BasicAuthHeaderParsingException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    public BasicAuthHeaderParsingException() {
        super("Error in Authorization header, it could not be parsed");
    }
    
}