package com.ptaas.controller;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ptaas.repository.LoadGenerator;
import com.ptaas.service.LoadGeneratorSvc;


@Controller
@RequestMapping("/loadgenerators")
public class LoadGeneratorController {

	@Autowired
	private LoadGeneratorSvc loadGeneratorSvc;
	
	@RequestMapping(method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE,params="query=all")
	@ResponseBody
	public Collection<LoadGenerator> getAllLoadGenerators() {
		return loadGeneratorSvc.getAllLoadGenerators();
	}
	
	@RequestMapping(method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE,params="query=available")
	@ResponseBody
	public Collection<LoadGenerator> getAllAvailableLoadGenerators() {
		return loadGeneratorSvc.getAllAvailableLgs();
	}
	
	@RequestMapping(method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE,params="query=one")
	@ResponseBody
	public LoadGenerator getOneAvailableLoadGenerator() {
		return loadGeneratorSvc.getOneAvailableLg();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/{name}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public LoadGenerator getLgDetails(@NotNull @PathVariable String name) {
		return loadGeneratorSvc.getAvailability(name);
	}
	
}
