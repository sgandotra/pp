package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.enums.EnumBase;
import com.ptaas.util.CommonUtils;



public enum SystemMemoryStatisticsType implements EnumBase<SystemMemoryStatisticsType> {
	MEMORY("mem");
	private String	text;
	
	SystemMemoryStatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemMemoryStatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemMemoryStatisticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemMemoryStatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<SystemMemoryStatisticsType>(EnumSet.allOf(SystemMemoryStatisticsType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemMemoryStatisticsType> getAllValues=getAll();
		for (SystemMemoryStatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
