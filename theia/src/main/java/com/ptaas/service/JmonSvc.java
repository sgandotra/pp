package com.ptaas.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

import com.ptaas.model.JmonRequestActivate;
import com.ptaas.model.JmonResponseActivate;
import com.ptaas.model.JmonResponseDeActivate;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.service.MonitoringSvc.MyUrlConnectionClient;


@Service("jmonSvc")
public class JmonSvc {

	@Value("${hostmon.port}")
	private String HOSTMON_PORT;
	
	private static final Logger logger = LoggerFactory.getLogger(JmonSvc.class);
	
	public JmonResponseActivate activate(Host host, List<String> javaServices) {
		
		Assert.notNull(host, "Host cannot be null");
		Assert.notNull(javaServices,"Java services cannot be null");
		Assert.notEmpty(javaServices,"Collection (javaServices) cannot be empty");
		
		JmonRequestActivate jmon 	= new JmonRequestActivate();
		
		for(String serviceName : javaServices) {
			JmonRequestActivate.Services s = new JmonRequestActivate.Services();
			s.setName(serviceName);
			jmon.getServices().add(s);
		}
		
		HostMonService svc		  = createClient(host.getHostName());
		JmonResponseActivate jmonResponse = svc.activateJmon(jmon);
		
		logger.debug(" Jmon response : {}",jmonResponse);
		return jmonResponse;
	}
	
	public JmonResponseDeActivate deactivate(Host host, List<String> javaServices) {
		Assert.notNull(host, "Host cannot be null");
		
		HostMonService svc		  = createClient(host.getHostName());
		JmonResponseDeActivate jmonResponse = svc.deactivateJMon();
		
		logger.info("Jmon response (Deactivate) {} ",jmonResponse);
		
		return jmonResponse;
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
