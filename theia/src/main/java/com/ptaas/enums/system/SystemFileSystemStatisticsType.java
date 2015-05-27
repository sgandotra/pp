package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.enums.EnumBase;
import com.ptaas.util.CommonUtils;




public enum SystemFileSystemStatisticsType implements EnumBase<SystemFileSystemStatisticsType> {
	JFSFILE("jfsfile");
	private String	text;
	
	SystemFileSystemStatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemFileSystemStatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemFileSystemStatisticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemFileSystemStatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<SystemFileSystemStatisticsType>(EnumSet.allOf(SystemFileSystemStatisticsType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemFileSystemStatisticsType> getAllValues=getAll();
		for (SystemFileSystemStatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
