package com.monitoreverywhere.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Properties;


public final class ConfigurationReader {
    
    public static final String GRAPHITE_HOSTNAME 		 = "graphite.hostname";
    public static final String GRAPHITE_PORT     		 = "graphite.port";
    public static final String GRAPHITE_PREFIX   		 = "graphite.prefix";
    
    private static final String GRAPHITE_HOSTNAME_VALUE  = "stage2lp48.qa.paypal.com";
    private static final String GRAPHITE_PORT_VALUE 	 = "2003";
    private static final String configFile 	   	 		 = "configuration.prod.properties";
        
    private static final ConfigurationReader configReader = new ConfigurationReader();
    
    private Properties properties;
    private String graphitePort;
    private String graphiteHost;
    private String graphitePrefix;
    
    private ImmutableMap<String,String> map;
    
    private ConfigurationReader() {
	           	
	    properties = new Properties();
        try {           
        	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);
        	System.err.println("Found configuration file");
            properties.load(is);
            
        } catch (Exception ex) {
            System.err.println("Configuration file [" + configFile +"] could not be located");
        }
        
        Builder<String,String> builder      = new ImmutableMap.Builder<String, String>();
        setPort(builder);
        setHost(builder);
        setPrefix(builder);
        
        map = builder.build();
    }
    
    protected void setPort(Builder<String,String> builder) {
    	
    	graphitePort 	= System.getProperty(GRAPHITE_PORT);       
    	
    	if(null == graphitePort) 
         	graphitePort = (String) properties.getProperty(GRAPHITE_PORT);
         
    	graphitePort = validatePort(graphitePort) ? graphitePort :GRAPHITE_PORT_VALUE;
        builder.put(GRAPHITE_PORT, graphitePort);
         
    }
    
    protected void setHost(Builder<String,String> builder) {
    	
    	graphiteHost 	= System.getProperty(GRAPHITE_HOSTNAME);    	  
    	
    	if(null == graphiteHost) 
        	graphiteHost = (String) properties.get(GRAPHITE_HOSTNAME);
        
        graphiteHost = validateHost(graphiteHost,graphitePort) ? graphiteHost : GRAPHITE_HOSTNAME_VALUE; 
        builder.put(GRAPHITE_HOSTNAME,graphiteHost);
        
    }
    
    protected void setPrefix(Builder<String,String> builder) {
    	
    	    	
	    graphitePrefix  = getPrefix();
    	
    	if(null == graphitePrefix) 
        	graphitePrefix = (String) properties.getProperty(GRAPHITE_PREFIX);
    	
    	
    	graphitePrefix = (graphitePrefix == null) ? getPrefix() : graphitePrefix;
    	
       	builder.put(GRAPHITE_PREFIX, graphitePrefix);
       
    }
    
    
    private String getPrefix() {
    	String customFolder = System.getenv("graphite.folder");
    	String hostName   	= System.getenv("HOSTNAME");
    	
    	if(null != customFolder) {
    		return hostName + "." +customFolder;
    	}
    	
    	if(null == hostName) {
    		try {
    			hostName = InetAddress.getLocalHost().getHostName();
    		} catch(Exception ex) {
    			System.err.println("Could not get hostname");
    		}
    	}
    	
    	System.err.println("user did not define a folder using hostname " +  hostName + " as default");
    	return hostName;
    }
    
    private boolean validatePort(String brokerPort) {
        boolean isValid;
        
        if (null == brokerPort || brokerPort.length() == 0)
            isValid = false;
        
        try {
            Integer.valueOf(brokerPort);
            isValid = true;
        } catch (NumberFormatException ex) {
            isValid = false;
        }
               
        return isValid;
    }
    
    private boolean validateHost(String hostName,String port) {
    	boolean isValid = false;
    	
    	if(null == hostName || hostName.length() == 0) 
    		isValid = false;
    	else {
    		InetSocketAddress address = new InetSocketAddress(hostName,Integer.valueOf(port)); 
    	
    		if(address.getAddress() != null)
    			isValid = true;
    	}
    	
    	
    	return isValid;
    }
    
    public static ConfigurationReader getInstance() {
        return configReader;
    }
    
    public ImmutableMap<String,String> getProperties() {
        return map;
    }
    
}
