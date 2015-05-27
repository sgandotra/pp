package com.ptaas.service;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.repository.StageMachine;
import com.ptaas.repository.StageMachineRepository;

@Service("stageMachineSvc")
public class StageMachineSvc {
	
	@Autowired
	private StageMachineRepository stageMachineRepository;
	
	public Collection<StageMachine> getAllStages() {
		
		return (Collection<StageMachine>) stageMachineRepository.findAll();
	}
	
	public Collection<StageMachine> getAllAvailableStages() {
		return stageMachineRepository.findByStatusAndInuse(true, false);
	}
	
	public Collection<StageMachine> getAllUnAvailableStages() {
		return stageMachineRepository.findByStatusAndInuse(true, true);
	}
	
	public StageMachine getOneAvailableStage() {
		return getAllAvailableStages().iterator().next();
	}
	
	public StageMachine getAvailability(String name) {
		
		Assert.hasText(name,"stage name cannot be null");
		
		return stageMachineRepository.findByStagename(name);
	}

	public void toggleInUse(StageMachine stageMachine , boolean flag) {
	    stageMachine.setDateModified(new Date());
	    stageMachine.setInuse(flag);
	    stageMachineRepository.save(stageMachine);
    }
	
}
