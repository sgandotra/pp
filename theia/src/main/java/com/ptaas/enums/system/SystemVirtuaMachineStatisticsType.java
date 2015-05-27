package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.enums.EnumBase;

import com.ptaas.util.CommonUtils;


public enum SystemVirtuaMachineStatisticsType implements EnumBase<SystemVirtuaMachineStatisticsType> {
	VIRTUAL_MACHINE("vm");
	private String	text;
	
	SystemVirtuaMachineStatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemVirtuaMachineStatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemVirtuaMachineStatisticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemVirtuaMachineStatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<SystemVirtuaMachineStatisticsType>(EnumSet.allOf(SystemVirtuaMachineStatisticsType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemVirtuaMachineStatisticsType> getAllValues=getAll();
		for (SystemVirtuaMachineStatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
