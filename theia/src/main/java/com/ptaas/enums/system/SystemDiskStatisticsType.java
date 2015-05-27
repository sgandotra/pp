package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.enums.EnumBase;
import com.ptaas.util.CommonUtils;



public enum SystemDiskStatisticsType implements EnumBase<SystemDiskStatisticsType> {
	DISK_BYTE_SIZE("diskbsize"),DISK_BUSY("diskbusy"),DISK_READ("diskread"),DISK_WRITE("diskwrite"),DISK_TRANSFER("diskxfer");
	private String	text;
	
	SystemDiskStatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemDiskStatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemDiskStatisticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemDiskStatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<SystemDiskStatisticsType>(EnumSet.allOf(SystemDiskStatisticsType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemDiskStatisticsType> getAllValues=getAll();
		for (SystemDiskStatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
