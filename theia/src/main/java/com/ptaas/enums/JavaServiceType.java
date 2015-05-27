package com.ptaas.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.util.CommonUtils;

public enum JavaServiceType implements EnumBase<JavaServiceType>{
	HELIX("helix"), SPARTA("sparta"),RAPTOR("raptor");
	private String	text;
	
	JavaServiceType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static JavaServiceType fromString(String text) {
		 return CommonUtils.getEnumFromString(JavaServiceType.class, text,HELIX.text);
	}
	public String toString()
	{
		return getText();
	}
	@Override
	public List<JavaServiceType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<JavaServiceType>(EnumSet.allOf(JavaServiceType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<JavaServiceType> getAllValues=getAll();
		for (JavaServiceType status : getAllValues) {
			result.add(status.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
