package com.ptaas.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.util.CommonUtils;

public enum ResponseCode implements EnumBase<ResponseCode> {
	SUCCESS("success"), REPORT_EXIST("report_exist"), INVALID_USER("invalid_user"),GENERAL_ERROR("general_error");
	private String	text;
	
	ResponseCode(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static ResponseCode fromString(String text) {
		 return CommonUtils.getEnumFromString(ResponseCode.class, text);
	}
	public String toString()
	{
		return getText();
	}

	@Override
	public List<ResponseCode> getAll() {
		// TODO Auto-generated method stub
		 return new ArrayList<ResponseCode>(EnumSet.allOf(ResponseCode.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<ResponseCode> getAllValues=getAll();
		for (ResponseCode responseCode : getAllValues) {
			result.add(responseCode.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
	
}
