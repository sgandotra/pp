package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.enums.EnumBase;
import com.ptaas.util.CommonUtils;



public enum SystemProcessStatisticsType implements EnumBase<SystemProcessStatisticsType> {
	PROCESS("proc");
	private String	text;
	
	SystemProcessStatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemProcessStatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemProcessStatisticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemProcessStatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<SystemProcessStatisticsType>(EnumSet.allOf(SystemProcessStatisticsType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemProcessStatisticsType> getAllValues=getAll();
		for (SystemProcessStatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
