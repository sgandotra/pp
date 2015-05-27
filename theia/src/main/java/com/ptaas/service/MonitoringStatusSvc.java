package com.ptaas.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import retrofit.RetrofitError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptaas.model.MonitoredHostsModel;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService;
import com.ptaas.repository.SystemConfiguration;
import com.ptaas.service.exception.SystemConfigNotFoundException;

@Service("monitoringStatusSvc")
public class MonitoringStatusSvc extends MonitoringSvc {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringStatusSvc.class);
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    /**
     * 
     * @param configName
     * @return
     * @throws SystemConfigNotFoundException
     */
    public MonitoredHostsModel getStatusByConfig(final String configName) throws SystemConfigNotFoundException {
        Assert.hasText(configName,"config cannot be empty");
        
        SystemConfiguration configuration = systemConfigService.findByName(configName);

        if(null == configuration) 
            throw new SystemConfigNotFoundException(" Configuration :" + configName +" not found");
        
        Map<String,Host> monitoredHosts     = new HashMap<String,Host>();        
        Set<HostMonServiceStatus> tasks     = new HashSet<HostMonServiceStatus>();
        
        logger.info("Fetching configuration details with config name : {}",configName);
        MonitoredHostsModel model           = new MonitoredHostsModel();
        model.setConfigName(configName);
        model.setRunning(configuration.isRunning());
        model.setFavorite(configuration.isFavorite());
        
        try {
            
            for(com.ptaas.repository.Host host : configuration.getHosts()) {
                
                Host monitoredHost     = new Host();
                monitoredHost.setHostName(host.getHostName());               
                monitoredHost.setMonitoredServices(new HashSet<MonitoredService>());
                monitoredHost.setNmon(host.isNmon());
                monitoredHost.setNmonStart(host.getNmonstart());
                monitoredHost.setNmonEnd(host.getNmonend());
                
                String componentStatus      = host.getComponentstatus();                
                ObjectMapper mapper         = new ObjectMapper();    
                @SuppressWarnings("unchecked")
                HashMap<String,Boolean> map = mapper.readValue(componentStatus, HashMap.class);
                
                for(String serviceName : map.keySet()) {
                    MonitoredService monitoredService = new MonitoredService();
                    monitoredService.setHostName(host.getHostName());
                    monitoredService.setName(serviceName);
                    monitoredHost.getMonitoredServices().add(monitoredService);
                }
                                
                logger.info("Fetching status for host : {}",host.getHostName());                
                HostMonServiceStatus hostMonServiceStatus = new HostMonServiceStatus(monitoredHost);
                tasks.add(hostMonServiceStatus);   
            }
            
            List<Future<Host>> futures = executors.invokeAll(tasks);
            for(Future<Host> future : futures) {
                Host host = future.get();
                monitoredHosts.put(host.getHostName(),host);
            }
            
            model.setHosts(monitoredHosts);
                        
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return model;
    }
    
    /**
     * For each host that is part of the system config, we maintain a list of services which are deployed 
     * on the host machine. This is a subset of services that have been configured in the system configuration.
     * For example a service A maybe part of system configuration but if it's not deployed it will not be part of the 
     * this Collection.
     * 
     * This Callable goes through the Collection finds the service type and 
     * deployed location on the host machine and returns it back to the client. The client can then find an
     * intersection between this list and the system configuration list and activate only the ones
     * that are actually deployed on the host
     * 
     * 
     * @author sagandotra
     *
     */    
    private class HostMonServiceStatus implements Callable<Host> {

        private Host monitoredHost;
        
        public HostMonServiceStatus(Host monitoredHost) {
            this.monitoredHost = monitoredHost;
        }
        
        @Override
        public Host call() throws Exception {
            Collection<MonitoredService> monitoredServices = null;
            try {
                final HostMonService svc = createClient(monitoredHost.getHostName());
                monitoredServices        = svc.getServicesStatus(monitoredHost.getMonitoredServices());
            } catch(RetrofitError ex) {
                monitoredHost.setError(ex.getMessage());
            }
            monitoredHost.setMonitoredServices(monitoredServices);
            return monitoredHost;
        }
        
    }
    
}
