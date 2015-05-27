package com.pp.jmeter.plugin;

import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.util.JMeterUtils;


/**
 * 
 * This singleton instance is will hold the jmeter configuration
 * 
 * 
 * @author sagandotra
 *
 */
public class AgentConfigurer {

	private static final AgentConfigurer agentConfigurer = new AgentConfigurer();
	
	private String hostname;
	private int port = 0;
	private String folderName;
	private FileServer fileServer;
	
	private static final String DEFAULT_HOSTNAME = "stage2lp48.qa.paypal.com";
	private static final int DEFAULT_PORT	= 2003;
	
	private AgentConfigurer() {
		fileServer = FileServer.getFileServer();
	}
	
	public static AgentConfigurer getInstance() {
		return agentConfigurer;
	}
	
	public void setHost(final String hostname, final int port) {
		this.hostname = hostname;
		this.port     = port;
	}
	
	public int getPort() {
		if(port < 999)
			return DEFAULT_PORT;
		return this.port;
	}
	
	public String getHostName() {
		if(this.hostname == null || this.hostname.trim().isEmpty())
			return DEFAULT_HOSTNAME;
		return this.hostname;
	}

	public String getFolderName() {
		if(this.folderName == null || this.folderName.trim().isEmpty()) {
			String folderName = JMeterUtils.getLocalHostIP() + "_" + fileServer.getScriptName();
			return folderName.replace('.', '_');
		}
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
}
