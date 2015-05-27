package com.pp.jmeter.plugin.exception;

public class InvalidConfigurationFileException extends Exception {

	private static final long serialVersionUID = 8647746721457527119L;
	
	public InvalidConfigurationFileException(String exception) {
		super(exception);
	}
	
	public InvalidConfigurationFileException(String exception, Throwable t) {
		super(exception,t);
	}
	
	public InvalidConfigurationFileException(Exception ex) {
		super(ex);
	}
	
}
