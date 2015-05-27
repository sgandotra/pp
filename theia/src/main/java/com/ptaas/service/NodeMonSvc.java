package com.ptaas.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

import com.ptaas.model.NodemonRequestActivate;
import com.ptaas.model.NodemonResponseActivate;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService.Type;
import com.ptaas.service.MonitoringSvc.MyUrlConnectionClient;

@Service("nodeMonSvc")
public class NodeMonSvc {

	private static final Logger logger = LoggerFactory.getLogger(NodeMonSvc.class);

	
	@Value("${hostmon.port}")
	private String HOSTMON_PORT;
	
	
	
	public NodemonResponseActivate activate(Host host, List<String> nodeMonServices) {
		
		
		Assert.notNull(host, "Host cannot be null");
		Assert.notNull(nodeMonServices,"Node Mon services cannot be null");
		Assert.notEmpty(nodeMonServices,"Collection (nodemonServices) cannot be empty");
		
		NodemonRequestActivate nodemon 	= new NodemonRequestActivate();
		
		for(String serviceName : nodeMonServices) {
			NodemonRequestActivate.Services s = new NodemonRequestActivate.Services();
			s.setName(serviceName);
			nodemon.getServices().add(s);
		}
		
		HostMonService svc				= createClient(host.getHostName());
		NodemonResponseActivate nodemonresponse = svc.activateNodemon(nodemon);
		
		logger.debug(" Node mon activate response : {}",nodemonresponse);
		
		return nodemonresponse;
	}
	
	public NodemonResponseActivate deactivate(Host host, List<String> nodeMonServices) {
		
		Assert.notNull(host, "Host cannot be null");
		Assert.notNull(nodeMonServices,"Node Mon services cannot be null");
		Assert.notEmpty(nodeMonServices,"Collection (nodemonServices) cannot be empty");
		
		NodemonRequestActivate nodemon 	= new NodemonRequestActivate();
		
		for(String serviceName : nodeMonServices) {
			NodemonRequestActivate.Services s = new NodemonRequestActivate.Services();
			s.setName(serviceName);
			nodemon.getServices().add(s);
		}
		
		HostMonService svc				= createClient(host.getHostName());
		NodemonResponseActivate nodemonresponse = svc.deactivateNodemon(nodemon);
		
		logger.debug(" Node mon activate response : {}",nodemonresponse);
		
		return nodemonresponse;
	}
	
	private HostMonService createClient(String hostName) {
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://"+ hostName + ":" +HOSTMON_PORT)
		.setLogLevel(LogLevel.FULL)
		.setClient(new MyUrlConnectionClient())
		.build();
	
		return restAdapter.create(HostMonService.class);
	}

}
