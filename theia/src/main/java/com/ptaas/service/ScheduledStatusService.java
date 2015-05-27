package com.ptaas.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

import com.google.gson.Gson;
import com.ptaas.model.HostMonStatus;
import com.ptaas.model.HostMonStatus.NetworkInterfaces.Bond0;
import com.ptaas.model.HostMonStatus.NetworkInterfaces.Bond01;
import com.ptaas.model.HostMonStatus.NetworkInterfaces.Eth0;
import com.ptaas.model.HostMonStatus.NetworkInterfaces.Eth01;
import com.ptaas.repository.Component;
import com.ptaas.repository.Host;
import com.ptaas.repository.HostRepository;
import com.ptaas.repository.Role;


@Service("scheduledService")
public class ScheduledStatusService {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledStatusService.class);
	
	private static final ExecutorService executorService = Executors.newFixedThreadPool(20);

	private static final int HOSTMON_PORT = 3000;
	
	@Autowired
	private HostRepository hostRepository;
	
	
	
	
	private HostMonService createClient(String hostName) {
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://"+ hostName + ":" +HOSTMON_PORT)
		//.setLogLevel(LogLevel.FULL)
		.build();
	
		return restAdapter.create(HostMonService.class);
	}
	
	
	public void getHostInfo(final Integer id) {
		
		Thread t = new Thread() {
			
			@Override 
			public void run() {
				Host h 					= hostRepository.findOne(id);	
				if(null != h) {
					HostMonService svc 		= createClient(h.getHostName());
					HostMonStatus hostMonStatus = svc.getStatus();
					persistHostStatus(hostMonStatus,h.getId());
				}
			}
		};
		
		executorService.submit(t);
	}
	
	
//	@Scheduled(fixedDelay=60000)
	@Transactional
	public void updateComponentStatus() {
		
		Collection<Host> hosts = (Collection<Host>) hostRepository.findAll();
		
		for(Host host : hosts) {
			if(host.isActive()) {
				String hostName = host.getHostName().toLowerCase();
				Map<String,Boolean> map = new HashMap<String,Boolean>();
				for(Role role : host.getRoles()) {
					for(Component component : role.getComponents()) {
						HostMonService svc 			= createClient(hostName);
						Set<String> services 		= svc.getServicesList();
						if(services.contains(component.getName())) {
							map.put(component.getName(),true);
						}
					}
				}
				String json =new Gson().toJson(map);
				logger.info("json : {}",json);
				host.setComponentstatus(json);
				hostRepository.save(host);
			}
		}
			
	}
	
	
	//@Scheduled(fixedDelay=30000)
	public void updateHostStatus() {
		
		Collection<Host> hosts = (Collection<Host>) hostRepository.findAll();
		
		for(Host host : hosts) {
			
			try {
				String hostName = host.getHostName();
				logger.info("processing host : {}",hostName);
			
				HostMonService svc 			= createClient(hostName);
				
				HostMonStatus hostMonStatus = svc.getStatus();
				persistHostStatus(hostMonStatus,host.getId());
			} catch(Exception ex) {
				logger.error("failed processing host : {}",host.getHostName());
				logger.error("Error : {}",ex.getMessage());
				ex.printStackTrace();
				Host h = hostRepository.findOne(host.getId());
				h.setActive(false);
				hostRepository.save(h);
			}
			
		}
		
	}
	
	protected void persistHostStatus(HostMonStatus hostMonStatus ,Integer id ) {
		Host h = hostRepository.findOne(id);
		if(null != h) {
			
			h.setHostName(h.getHostName());
			h.setPlatform(hostMonStatus.getPlatform() + "-" + hostMonStatus.getArch());
			h.setUptime(hostMonStatus.getUptime());
			h.setLoadavg(Arrays.toString(hostMonStatus.getLoadavg()));
			
			
			//memory
			h.setTotalMem(Long.valueOf(hostMonStatus.getMemory().getTotalmem()));
			h.setFreeMem(Long.valueOf(hostMonStatus.getMemory().getFreemem()));
			
			//cpu
			h.setCpus(hostMonStatus.getCpus().length);
			h.setCpuModel(hostMonStatus.getCpus()[0].getModel());
			
			//network
			List<Bond0> bonds0 = hostMonStatus.getNetworkInterfaces().getBond0();
			List<Eth0> eth0 = hostMonStatus.getNetworkInterfaces().getEth0();
			
			if(null != bonds0 && bonds0.size() > 0) { 
				h.setEthAddress1(bonds0.get(0).getAddress());
			} else if (null != eth0 && eth0.size() > 0){
				h.setEthAddress1(eth0.get(0).getAddress());
			} else {
				h.setEthAddress1("N/A");
			}
			
			List<Bond01> bonds01 = hostMonStatus.getNetworkInterfaces().getBond01();
			List<Eth01> eth01 = hostMonStatus.getNetworkInterfaces().getEth01();
			
			if(null != bonds01 && bonds01.size() > 0) { 
				h.setEthAddress2(bonds01.get(0).getAddress());
			} else if(null != eth01 && eth01.size() > 0){
				h.setEthAddress2(eth01.get(0).getAddress());
			} else {
				h.setEthAddress2("N/A");
			}
			
			//lastupdated
			h.setLastUpdated(Long.valueOf(hostMonStatus.getLastupdated_utc()));
			
			//isactive
			h.setActive(true);
			
			hostRepository.save(h);
		}
	}
}
