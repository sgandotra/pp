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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ptaas.model.RoleModel;
import com.ptaas.repository.Role;
import com.ptaas.service.RoleService;
import com.ptaas.service.exception.InvalidRequestException;
import com.ptaas.service.exception.RoleExistsException;
import com.ptaas.service.exception.RoleNotFoundException;


@Controller
@RequestMapping("/roles")
public class RoleController {

	private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RoleService roleService;
	
	
	//READ
	@RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Collection<Role> get() {
		return roleService.findAll();
	}
	
	//READ
	@RequestMapping(value = "/role/{name}",method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Role get(@NotNull @PathVariable String name) {
		logger.info("Searching for name : {}",name);
		return roleService.findByName(name);
		
	}
	
	//CREATE
	@RequestMapping(method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> create(@Valid RoleModel roleModel) {
		
		try {
			roleService.createRole(roleModel);
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} catch(RoleExistsException ex) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);

		}
	}
	
	//UPDATE
	@RequestMapping(value = "/role/{id}", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> update(@PathVariable Integer id, @Valid RoleModel roleModel) {
		
		logger.info("Searching for id : {} ",id);
		
		try {
			roleService.updateRole(id, roleModel);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(RoleNotFoundException ex) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	//DELETE
	@RequestMapping(value = "/role/{id}", method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		
		try {
			roleService.deleteRole(id);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(RoleNotFoundException ex) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	//DELETE
	@RequestMapping(value = "/role/component/{id}", method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> delete(@PathVariable Integer id,@RequestParam("component") String name) {
		logger.info("Received request to delete component : ",name);
		
		try {
			roleService.deleteComponentsFromRole(id, name);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch(InvalidRequestException ex) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		} catch(Exception ex) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
}
