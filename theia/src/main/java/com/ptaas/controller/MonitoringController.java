package com.ptaas.controller;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ptaas.model.HostActivationResponse;
import com.ptaas.model.MonitoredHostsModel;
import com.ptaas.service.MonitoringStatusSvc;
import com.ptaas.service.MonitoringToggleSvc;
import com.ptaas.service.MonitoringSvc.Action;
import com.ptaas.service.exception.InvalidRequestException;
import com.ptaas.service.exception.SystemConfigNotFoundException;


@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

	@Autowired
	private MonitoringStatusSvc monitoringStatusSvc;
	
	@Autowired
	private MonitoringToggleSvc monitoringToggleSvc;
	
	private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);
	
	
	@ResponseBody
	@RequestMapping(value="/{config}" , method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public MonitoredHostsModel getStatus(@NotNull @PathVariable String config) throws SystemConfigNotFoundException {
	    
	    logger.info("Received request for config : {}",config);
	    return monitoringStatusSvc.getStatusByConfig(config); 
	    
	}
	
	
	@ResponseBody
	@RequestMapping(value="/activate", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String,HostActivationResponse> activate(@Valid @NotNull @RequestBody MonitoredHostsModel monitoredHostsModel) 
	        throws InterruptedException, SystemConfigNotFoundException, InvalidRequestException {
	
		String configName = monitoredHostsModel.getConfigName();
	    logger.info("Received request for activate config : {}",configName);
		
	    Map<String,HostActivationResponse> activationResponse = monitoringToggleSvc.process(monitoredHostsModel,Action.ACTIVATE);
		return activationResponse;
	}
	
	@ResponseBody
	@RequestMapping(value="/deactivate", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String,HostActivationResponse> deactivate(@Valid @NotNull @RequestBody MonitoredHostsModel monitoredHostsModel) 
	        throws InterruptedException, SystemConfigNotFoundException, InvalidRequestException {
	
	    String configName = monitoredHostsModel.getConfigName();
        logger.info("Received request for activate config : {}",configName);
        
        Map<String,HostActivationResponse> activationResponse = monitoringToggleSvc.process(monitoredHostsModel,Action.DEACTIVATE);
        return activationResponse;
	}
	
}
