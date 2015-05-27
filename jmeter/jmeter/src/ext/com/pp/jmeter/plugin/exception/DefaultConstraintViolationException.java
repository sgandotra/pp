package com.pp.jmeter.plugin.exception;

public class DefaultConstraintViolationException extends Exception {

	private static final long serialVersionUID = 7737855918516995172L;
	
	public DefaultConstraintViolationException(String exception) {
		super(exception);
	}
	
	public DefaultConstraintViolationException(Exception ex) {
		super(ex);
	}
	
	public DefaultConstraintViolationException(String exception, Throwable t) {
		super(exception,t);
	}
}
