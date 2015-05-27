package com.ptaas.service;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.repository.LoadGenerator;
import com.ptaas.repository.LoadGeneratorRepository;


@Service("loadGeneratorSvc")
public class LoadGeneratorSvc {
	
	@Autowired
	private LoadGeneratorRepository loadGeneratorRepository;
	
	public Collection<LoadGenerator> getAllLoadGenerators() {
		
		return (Collection<LoadGenerator>) loadGeneratorRepository.findAll();
	}
	
	public Collection<LoadGenerator> getAllAvailableLgs() {
		return loadGeneratorRepository.findByStatusAndInuse(true, false);
	}
	
	public Collection<LoadGenerator> getAllUnAvailableLgs() {
		return loadGeneratorRepository.findByStatusAndInuse(true, true);
	}
	
	public LoadGenerator getOneAvailableLg() {
		return getAllAvailableLgs().iterator().next();
	}
	
	public LoadGenerator getAvailability(String name) {
		
		Assert.hasText(name,"load generator name cannot be null");
		
		return loadGeneratorRepository.findByLgname(name);
	}
	
	public void toggleInUse(LoadGenerator loadGenerator , boolean flag) {
	    loadGenerator.setInuse(flag);
	    loadGenerator.setDateModified(new Date());
	    loadGeneratorRepository.save(loadGenerator);
	}


}
