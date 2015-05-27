package com.ptaas.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.util.CommonUtils;

public enum ServiceType implements EnumBase<ServiceType>{
	JAVA("java"), UNKNOWN("unknown"),NODE("node");
	private String	text;
	
	ServiceType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static ServiceType fromString(String text) {
		 return CommonUtils.getEnumFromString(ServiceType.class, text,UNKNOWN.text);
	}
	public String toString()
	{
		return getText();
	}
	@Override
	public List<ServiceType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<ServiceType>(EnumSet.allOf(ServiceType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<ServiceType> getAllValues=getAll();
		for (ServiceType status : getAllValues) {
			result.add(status.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
