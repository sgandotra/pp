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

import com.ptaas.repository.StageMachine;
import com.ptaas.service.StageMachineSvc;

@Controller
@RequestMapping("/sut")
public class StageMachineController {

	@Autowired
	private StageMachineSvc stageMachineSvc;
	
	@RequestMapping(method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE,params="query=all")
	@ResponseBody
	public Collection<StageMachine> getAllStageMachines() {
		return stageMachineSvc.getAllStages();
	}
	
	@RequestMapping(method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE,params="query=available")
	@ResponseBody
	public Collection<StageMachine> getAllAvailableStageMachines() {
		return stageMachineSvc.getAllAvailableStages();
	}
	
	@RequestMapping(method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE,params="query=one")
	@ResponseBody
	public StageMachine getOneAvailableStageMachine() {
		return stageMachineSvc.getOneAvailableStage();
	}
	
	@RequestMapping(method=RequestMethod.GET, value = "/{name}" , produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StageMachine getStageMachineDetails(@NotNull @PathVariable String name) {
		return stageMachineSvc.getOneAvailableStage();
	}
	
}
