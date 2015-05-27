package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.enums.EnumBase;
import com.ptaas.util.CommonUtils;


public enum SystemCPUStatisticsType implements EnumBase<SystemCPUStatisticsType> {
	CPU("cpu_all");
	private String	text;
	
	SystemCPUStatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemCPUStatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemCPUStatisticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemCPUStatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<SystemCPUStatisticsType>(EnumSet.allOf(SystemCPUStatisticsType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemCPUStatisticsType> getAllValues=getAll();
		for (SystemCPUStatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
