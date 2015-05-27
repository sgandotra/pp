package com.ptaas.controller;

import java.security.Principal;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ptaas.model.SystemConfigurationModel;
import com.ptaas.repository.SystemConfiguration;
import com.ptaas.service.SystemConfigService;
import com.ptaas.service.exception.SystemConfigExistsException;
import com.ptaas.service.exception.SystemConfigNotFoundException;

@Controller
@RequestMapping("/systemconfiguration")
public class SystemConfigController {

	private static final Logger logger = LoggerFactory.getLogger(SystemConfigController.class);
	
	@Autowired
	private SystemConfigService systemConfigService;
	
	
	//READ ALL
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Collection<SystemConfiguration> get(Principal user) {		
		logger.info("Returning all system configurations");	
		return systemConfigService.findAllByUser(user.getName());		
	}
	

	//CREATE
	@RequestMapping(method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<Void> create(@NotNull @Valid SystemConfigurationModel model) {
		
		try {
			systemConfigService.createSystemConfig(model);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(SystemConfigExistsException ex) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
	}
	
	
	//READ
	@ResponseBody
	@RequestMapping(value="/config/id/{id}",method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public SystemConfiguration get(@NotNull @PathVariable Integer id) {
		logger.info("Searching for config id : {}",id);
		
		return systemConfigService.get(id);
	}
	
	//READ
	@ResponseBody
	@RequestMapping(value="/config/name/{name}",method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public SystemConfiguration get(@NotNull @PathVariable String name) {
		logger.info("Searching for config id : {}",name);
		
		return systemConfigService.findByName(name);
	}
	
	//UPDATE
	@RequestMapping(value="/config/{id}",method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> edit(@NotNull @PathVariable Integer id, @NotNull @Valid SystemConfigurationModel model) {
		try {
			systemConfigService.updateSystemConfig(id, model);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(SystemConfigNotFoundException ex) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	
	
	//DELETE
	@RequestMapping(value="/config/{id}",method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@NotNull @PathVariable Integer id) {
		logger.info("Searching for config id : {}",id);
		
		try {
			systemConfigService.deleteSysConfig(id);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(SystemConfigNotFoundException ex) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
}
