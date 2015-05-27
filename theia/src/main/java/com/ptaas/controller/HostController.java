package com.ptaas.controller;

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

import com.ptaas.model.HostModel;
import com.ptaas.repository.Host;
import com.ptaas.service.HostService;
import com.ptaas.service.exception.HostExistsException;
import com.ptaas.service.exception.HostNotFoundException;


@Controller
@RequestMapping("/hosts")
public class HostController {

	private static final Logger logger = LoggerFactory.getLogger(HostController.class);
	
	@Autowired
	private HostService hostService;
	
	
	
	//READ
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Collection<Host> get() {
		return hostService.findAll();
	}
	
	//READ
	@RequestMapping(value = "/host/{id}",method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Host get(@NotNull @PathVariable Integer id) {
		logger.info("Searching for id : {}",id);
		return hostService.get(id);
		
	}
	
	//READ
	@RequestMapping(value = "/{name}",method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Host get(@NotNull @PathVariable String name) {
		logger.info("Searching for name : {}",name);
		return hostService.findByHostName(name);
	}
	
	//CREATE
	@RequestMapping(method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> create(@Valid HostModel hostModel) {
		
		logger.info("calling in create : {}",hostModel);
		try {
			hostService.createHost(hostModel);
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} catch(HostExistsException ex) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
	}
	
	//DELETE
	@RequestMapping(value = "/{id}", method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		
		try {
			hostService.deleteHost(id);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(HostNotFoundException ex) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

		}
		
	}
	

	//UPDATE
	@RequestMapping(value = "/host/{id}", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> update(@PathVariable Integer id, @Valid HostModel hostModel) {
		
		try {
			hostService.updateHost(id, hostModel);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(HostNotFoundException ex) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} 
		
	}
	
}
