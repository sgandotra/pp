package com.monitoreverywhere.config;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ConfigurationReaderTest {

	private static final String VALID_ENV_HOSTNAME = "www.ebay.com";
	private static final String VALID_ENV_PORT     = "443";
	private static final String VALID_ENV_PREFIX   = "envprefix";
	
	private static final String VALID_PROPERTY_FILE_HOSTNAME = "www.paypal.com";
	private static final String VALID_PROPERTY_FILE_PORT     = "80";
	private static final String VALID_PROPERTY_FILE_PREFIX   = "graphiteprefix";
	
	@Test
	public void testPropertiesFile() {
		ConfigurationReader configuration = ConfigurationReader.getInstance();
		ImmutableMap<String,String> map   = configuration.getProperties();
		
		assertEquals(VALID_PROPERTY_FILE_HOSTNAME,map.get(ConfigurationReader.GRAPHITE_HOSTNAME));
		assertEquals(VALID_PROPERTY_FILE_PORT,map.get(ConfigurationReader.GRAPHITE_PORT));
		assertEquals(VALID_PROPERTY_FILE_PREFIX,map.get(ConfigurationReader.GRAPHITE_PREFIX));
		
	}
	

	@Test
	public void testSystemEnv() {
	
		System.setProperty(ConfigurationReader.GRAPHITE_HOSTNAME, VALID_ENV_HOSTNAME);
		System.setProperty(ConfigurationReader.GRAPHITE_PORT, VALID_ENV_PORT);
		System.setProperty(ConfigurationReader.GRAPHITE_PREFIX, VALID_ENV_PREFIX);
		
		ConfigurationReader configuration = ConfigurationReader.getInstance();
		ImmutableMap<String,String> map   = configuration.getProperties();
		
		assertEquals(VALID_ENV_HOSTNAME,map.get(ConfigurationReader.GRAPHITE_HOSTNAME));
		assertEquals(VALID_ENV_PORT,map.get(ConfigurationReader.GRAPHITE_PORT));
		assertEquals(VALID_ENV_PREFIX,map.get(ConfigurationReader.GRAPHITE_PREFIX));
		
	}

}
