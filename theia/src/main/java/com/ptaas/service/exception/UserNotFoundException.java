package com.ptaas.service.exception;

public class UserNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1785400880078775744L;
    
    public UserNotFoundException(String msg) {
        super(msg);
    }

}
