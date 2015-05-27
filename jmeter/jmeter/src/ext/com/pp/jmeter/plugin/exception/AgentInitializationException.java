package com.pp.jmeter.plugin.exception;

public class AgentInitializationException extends Exception {

	private static final long serialVersionUID = 3294888517992872289L;
	
	
	public AgentInitializationException(String exception) {
		super(exception);
	}
	
	public AgentInitializationException(String exception , Throwable t) {
		super(exception, t);
	}
	
	public AgentInitializationException(Exception ex) {
		super(ex);
	}
}
