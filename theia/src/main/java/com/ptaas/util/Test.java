package com.ptaas.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
	private static final String METRICES="metrices";
	private static final String DOT=".";
	private static final String ALL="all";
	private static final String PREFIX="prefix";
	private static final String STAGE_NAME="${stage_name}";
	private static final String SERVICE_NAME="${service_name}";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyUtil propertyUtil=PropertyUtil.getInstance();
		Map<String,HashMap<String,String>> metricesMap=new HashMap<String,HashMap<String,String>>();
		String metricesAll=propertyUtil.getParameterValue(METRICES+DOT+ALL, "system");
		List<String> metricesList = Arrays.asList(metricesAll.split(","));
		for (String metrice : metricesList) {
			System.out.println("metrices : "+metrice);
		}
		
	}

}
