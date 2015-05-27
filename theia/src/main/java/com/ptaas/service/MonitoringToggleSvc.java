package com.ptaas.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.model.HostActivationResponse;
import com.ptaas.model.JmonResponseActivate;
import com.ptaas.model.JmonResponseDeActivate;
import com.ptaas.model.MonitoredHostsModel;
import com.ptaas.model.NodemonResponseActivate;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService.Type;
import com.ptaas.repository.SystemConfiguration;
import com.ptaas.service.exception.InvalidRequestException;
import com.ptaas.service.exception.SystemConfigNotFoundException;

@Service("monitoringToogleSvc")
public class MonitoringToggleSvc extends MonitoringSvc {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoringToggleSvc.class);
    
    @Autowired
    private SystemConfigService systemConfigService;    
    
    @Autowired
    private HostService hostService;
    
    @Autowired
    private NmonSvc nmonSvc;
    
    @Autowired
    private NodeMonSvc nodeMonSvc;
    
    @Autowired
    private JmonSvc jmonSvc;
    
    /**
     *  Process request to activate or deactivate Hosts
     * 
     * 
     * @param monitoredHostsModel
     * @param action
     * @return
     * @throws InterruptedException
     * @throws InvalidRequestException 
     * @throws SystemConfigNotFoundException 
     */    
    public Map<String,HostActivationResponse> process(MonitoredHostsModel monitoredHostsModel,Action action) 
            throws InterruptedException, SystemConfigNotFoundException, InvalidRequestException {
        
        Assert.notNull(monitoredHostsModel, "monitoredHostsModel cannot be null");
        
        validateRequest(monitoredHostsModel.getConfigName(),action);
        
        Map<String,HostActivationResponse> responses = new HashMap<String,HostActivationResponse>();
        Set<HostMonActionCallable> tasks             = new HashSet<HostMonActionCallable>(); 
        
       for(String hostName : monitoredHostsModel.getHosts().keySet()) {
           HostMonActionCallable task = new HostMonActionCallable(monitoredHostsModel.getConfigName(),
                   action,
                   monitoredHostsModel.getHosts().get(hostName));
           tasks.add(task);
       }
       
       List<Future<HostActivationResponse>> futures = executors.invokeAll(tasks);
       
        for(Future<HostActivationResponse> future : futures) {
            HostActivationResponse response;
            try {
                response = future.get(HOSTMON_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch(Exception ex) {
                response = new HostActivationResponse();
                response.setErrMsg(ex.getMessage());
            }
            responses.put(response.getHost().getHostName(),response);
        }
        
        boolean flag = (action == Action.ACTIVATE) ? true : false;
        updateSystemConfigStatus(monitoredHostsModel.getConfigName(),flag);

        return responses;
    }
    
    public class HostMonActionCallable implements Callable<HostActivationResponse> {

        private String configName;
        private Host host;
        private Action action;
        
        public HostMonActionCallable(String configName, Action action, Host host) {
            
            Assert.hasText(configName,"config name cannot be empty");
            Assert.notNull(action, "Action cannot be null");
            Assert.notNull(host, "host cannot be empty");
            
            this.configName = configName;
            this.action     = action;
            this.host       = host;                 
        }
        
        @Override
        public HostActivationResponse call() throws Exception {
            
            HostActivationResponse response;
            
            if(action.equals(Action.ACTIVATE))
                response = activateByHost(configName,host);
            else
                response = deactivateByHost(configName,host);
            
            return response;
        }
        
    }
    
    /**
     * 
     * @param configName
     * @param host
     * @return
     */    
    public HostActivationResponse activateByHost(String configName, Host host) {
        logger.info("Received request to activate host : {}",host.getHostName());
        
        HostActivationResponse response = new HostActivationResponse();
        SystemConfiguration configuration = systemConfigService.findByName(configName);
        response.setConfigName(configuration.getName());

        
        EnumMap<Type,List<String>> services = new EnumMap<Type,List<String>>(Type.class);
        HostActivationResponse.Host hostMon = new HostActivationResponse.Host();
        hostMon.setHostName(host.getHostName());
        
        for(MonitoredService monitoredService : host.getMonitoredServices() ) {
            logger.info("service : {} {} " + monitoredService.getType() ,monitoredService.getHostName(),monitoredService.getLocation());
            if(services.get(monitoredService.getType()) == null) {
                services.put(monitoredService.getType(), new ArrayList<String>());
            }
            services.get(monitoredService.getType()).add(monitoredService.getName());
        }
        
        if(host.isNmon()) {
            hostMon.setNmonResponseActivate(nmonSvc.activate(host));
        }
        
        for(Type type : services.keySet()) {
        
            if(type.equals(Type.JAVA)) {
            //starting jmon
            JmonResponseActivate jmonResponse = jmonSvc.activate(host,services.get(Type.JAVA));
            hostMon.setJmonResponseActivate(jmonResponse);
            
            }
            else if (type.equals(Type.NODE)) {
                //starting nodemon
                NodemonResponseActivate nodemonresponse = nodeMonSvc.activate(host,services.get(Type.NODE));
                hostMon.setNodeMonResponseActivate(nodemonresponse);
                
            } else {}
        }       
        response.setHost(hostMon);
        return response;
        
    }
    
    
    public HostActivationResponse deactivateByHost(String configName, Host host) {
        
        HostActivationResponse response = new HostActivationResponse();
        
        SystemConfiguration configuration = systemConfigService.findByName(configName);
        response.setConfigName(configuration.getName());

        
        EnumMap<Type,List<String>> services = new EnumMap<Type,List<String>>(Type.class);
        HostActivationResponse.Host hostMon = new HostActivationResponse.Host();
        hostMon.setHostName(host.getHostName());
        
        for(MonitoredService monitoredService : host.getMonitoredServices() ) {
            logger.info("service : {} {} " + monitoredService.getType() ,monitoredService.getHostName(),monitoredService.getLocation());
            if(services.get(monitoredService.getType()) == null) {
                services.put(monitoredService.getType(), new ArrayList<String>());
            }
            services.get(monitoredService.getType()).add(monitoredService.getName());
        }
        
        if(host.isNmon()) {
            hostMon.setNmonResponseActivate(nmonSvc.deActivate(host));

        }
        
        for(Type type : services.keySet()) {
        
            if(type.equals(Type.JAVA)) {
            //starting jmon
            JmonResponseDeActivate jmonResponse = jmonSvc.deactivate(host,services.get(Type.JAVA));
            hostMon.setJmonResponseDeActivate(jmonResponse);
            
            }
            else if (type.equals(Type.NODE)) {
                //starting nodemon
                NodemonResponseActivate nodemonresponse = nodeMonSvc.deactivate(host,services.get(Type.NODE));
                hostMon.setNodeMonResponseActivate(nodemonresponse);
                
            } else {}
        }
        response.setHost(hostMon);    
        return response;
        
    }

    private void updateSystemConfigStatus(String configName, boolean flag) {
        
        Assert.notNull(configName, " Configuration name cannot be null");
        
        logger.info("Updating system config status of {} to {}",configName,flag);
        
        try {
            systemConfigService.changeRunningStatus(configName, flag);
        } catch (SystemConfigNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private void validateRequest(String configName, Action action) throws SystemConfigNotFoundException, InvalidRequestException {
        SystemConfiguration configuration = systemConfigService.findByName(configName);
        
        if(null == configuration) 
            throw new SystemConfigNotFoundException(" Configuration :" + configName +" not found");
        
        boolean isRunning = configuration.isRunning();        
        if(isRunning && action.equals(Action.ACTIVATE) || !isRunning && action.equals(Action.DEACTIVATE))
            throw new InvalidRequestException("Current state and requested action on the system configuration are same");
        
    }

}
