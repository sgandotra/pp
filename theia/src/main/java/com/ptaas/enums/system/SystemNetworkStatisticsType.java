package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ptaas.enums.EnumBase;
import com.ptaas.util.CommonUtils;


public enum SystemNetworkStatisticsType implements EnumBase<SystemNetworkStatisticsType> {
	NETWORK("net"),NETWORK_PACKET("netpacket");
	private String	text;
	
	SystemNetworkStatisticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemNetworkStatisticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemNetworkStatisticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemNetworkStatisticsType> getAll() {
		// TODO Auto-generated method stub
		return new ArrayList<SystemNetworkStatisticsType>(EnumSet.allOf(SystemNetworkStatisticsType.class));
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemNetworkStatisticsType> getAllValues=getAll();
		for (SystemNetworkStatisticsType statistics : getAllValues) {
			result.add(statistics.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
}
