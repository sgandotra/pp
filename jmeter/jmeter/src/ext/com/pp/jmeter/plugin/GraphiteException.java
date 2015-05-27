package com.pp.jmeter.plugin;

public class GraphiteException extends Exception {

	private static final long serialVersionUID = -8683495934114648205L;

	public GraphiteException(String msg) {
		super(msg);
	}
	
	public GraphiteException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
}
