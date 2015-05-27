package com.ptaas.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


public class JmonResponseDeActivate {

	private String timestamp;
	
	private List<String> jmonresponse;
	
	public JmonResponseDeActivate() {}	

	public List<String> getJmonresponse() {
		return jmonresponse;
	}

	public void setJmonresponse(List<String> jmonresponse) {
		this.jmonresponse = jmonresponse;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
