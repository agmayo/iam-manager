package org.acme.iam.manager.exceptions;

import javax.ws.rs.NotFoundException;

public class UserException extends NotFoundException {

 
    /**
     *
     */
    private static final long serialVersionUID = -5059510496448091042L;


    public UserException(String message) {
        super(message);
    }

    public UserException() {
        super("User not found");

    }
    

}