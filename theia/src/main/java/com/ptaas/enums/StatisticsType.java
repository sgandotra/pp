package com.ptaas.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ptaas.enums.system.SystemStaticsType;
import com.ptaas.util.CommonUtils;

public enum StatisticsType implements EnumBase<StatisticsType> {
	SYSTEM("system"), JAVA("java"), NODE("node"), JMETER("jmeter"), OTHER(
			"other");
	
	private String	text;
	
	StatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static StatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(StatisticsType.class, text,OTHER.text);
	}
	
	
	public String toString() {
		return getText();
	}
	
	@Override
	public List<StatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<StatisticsType>(
				EnumSet.allOf(StatisticsType.class));
	}
	
	@Override
	public List<String> getAllValues() {
		List<String> result = new ArrayList<String>();
		List<StatisticsType> getAllValues = getAll();
		for (StatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
	
	public List<String> getTargets(List<String> stringList) {
		List<String> resultList = new ArrayList<String>();
		if (fromString(text) == SYSTEM) {
			List<String> intermediateList = SystemStaticsType.CPU
					.getTargets(stringList);
			for (String string : intermediateList) {
				resultList.add(text + "." + string);
			}
		}
		return resultList;
	}
	
	public static StatisticsType fromValue(String v) {
		return valueOf(v);
	}
	
}
