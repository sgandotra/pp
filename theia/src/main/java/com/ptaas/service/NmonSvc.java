package com.ptaas.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

import com.ptaas.model.NmonRequestActivate;
import com.ptaas.model.NmonResponseActivate;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.service.MonitoringSvc.MyUrlConnectionClient;
import com.ptaas.service.exception.HostNotFoundException;

@Service("nmonSvc")
public class NmonSvc {
	
	@Autowired
	private HostService hostService;
	
	@Value("${hostmon.port}")
	private String HOSTMON_PORT;
	
	@Value("${hostmon.nmon.activation.default}")
	private String defaultActivation;
	
	@Value("${hostmon.nmon.default.frequency}")
	private String defaultFrequency;
	
	private static final Logger logger = LoggerFactory.getLogger(NmonSvc.class);
	
	public NmonResponseActivate activate(Host host) {
		
		Assert.notNull(host, "Host cannot be null");
		
		NmonRequestActivate nmon = new  NmonRequestActivate();
		nmon.getNmon().setFrequency(defaultFrequency);
		nmon.getNmon().setSamples(getSamples(host.getNmonStart(),host.getNmonEnd()));
		nmon.getNmon().setGraphite("true");
		
		HostMonService svc 		  = createClient(host.getHostName());
		
		NmonResponseActivate nmonResponse =  svc.activateNmon(nmon);
		
		
		if(nmonResponse.getNmonStatus().getStatus().equals("ACTIVATED")) {
			updateDBStatus(nmonResponse,host);
		}
		
		logger.info("Get Status : {}",nmonResponse.getStatus());
		return nmonResponse;
	}
	
	public NmonResponseActivate deActivate(Host host) {
	
		Assert.notNull(host, "Host cannot be null");
		
		HostMonService svc 		  = createClient(host.getHostName());
		
		NmonResponseActivate nmonResponse =  svc.deactivateNmon();
		
		if(nmonResponse.getNmonStatus().getStatus().equals("INACTIVE")) {
			updateDBStatus(nmonResponse,host);
		}
		
		logger.info("Get Status : {}",nmonResponse.getStatus());
		return nmonResponse;
	}


	private void updateDBStatus(NmonResponseActivate nmonResponse,Host host) {
		
		long nmonStart = nmonResponse.getNmonStatus().getStarted();
		long nmonEnd   = nmonResponse.getNmonStatus().getEnded();
		
		try {
			hostService.updateNmon(host.getHostName(),nmonStart , nmonEnd);
		} catch (HostNotFoundException ex) {
			logger.error("DB could not be updated");
			ex.printStackTrace();
		}
	}
	
	
	private HostMonService createClient(String hostName) {
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://"+ hostName + ":" +HOSTMON_PORT)
		.setLogLevel(LogLevel.FULL)
		.setClient(new MyUrlConnectionClient())
		.build();
	
		return restAdapter.create(HostMonService.class);
	}
	
	private String getSamples(long start, long end) {
		
	    Date d1 = new Date(start);
	    Date d2 = new Date(end);
	    Date now = new Date();
	    
		logger.info("Request Nmon activation date : {} , Deactivation date {} ",d1,d2);		
		
		if(d1.before(now))
		    d1 = now;
		
        if(d2.before(now) || d2.before(d1)) {
        	logger.error("requested date cannot be after present time , using default");
        	return defaultActivation;
        }
            
        return String.valueOf((d2.getTime() - d1.getTime())/10000);
		

	}
	
}
