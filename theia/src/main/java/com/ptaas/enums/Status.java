package com.ptaas.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.util.CommonUtils;

public enum Status implements EnumBase<Status>{
	success("success"), failed("failed"), partial("partial");
	private String	text;
	
	Status(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static Status fromString(String text) {
		 return CommonUtils.getEnumFromString(Status.class, text);
	}
	public String toString()
	{
		return getText();
	}
	@Override
	public List<Status> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<Status>(EnumSet.allOf(Status.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<Status> getAllValues=getAll();
		for (Status status : getAllValues) {
			result.add(status.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
