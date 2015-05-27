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

import com.ptaas.repository.Component;
import com.ptaas.service.ComponentService;
import com.ptaas.service.exception.ComponentExistsException;
import com.ptaas.service.exception.ComponentNotFoundException;


@Controller
@RequestMapping("/components")
public class ComponentController {

	private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);
	
	@Autowired
	private ComponentService componentService;
	
	//READ ALL
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Collection<Component> get() {
		return componentService.findAll();
	}
	
	//READ
	@RequestMapping(value = "/component/{name}",method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Component get(@NotNull @PathVariable String name) {
		return componentService.findComponent(name);
		
	}
	
	//CREATE
	@RequestMapping(method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> create(@Valid Component component) {
		
		try {
			componentService.createComponent(component);
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} catch(ComponentExistsException ex) {
			logger.info("Component {} exists",component);
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
	}
	
	//UPDATE
	@RequestMapping(value = "/component/{id}", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> update(@PathVariable Integer id, @Valid Component component) {
		

		try {
			componentService.updateComponent(id,component);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(ComponentNotFoundException ex) {
			logger.info("Component {} does not exist",component);
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	//DELETE
	@RequestMapping(value = "/component/{id}", method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		
		try {
			componentService.deleteComponent(id);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(ComponentNotFoundException ex) {
			logger.info("Component {} does not exist",id);
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		
	}
}
