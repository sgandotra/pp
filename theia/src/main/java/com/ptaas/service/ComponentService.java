package com.ptaas.service;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.repository.Component;
import com.ptaas.repository.ComponentRepository;
import com.ptaas.repository.Role;
import com.ptaas.service.exception.ComponentExistsException;
import com.ptaas.service.exception.ComponentNotFoundException;


@Service("componentService")
public class ComponentService {
	
	@Autowired
	private ComponentRepository componentRepository;

	public Collection<Component> findAll() {
		return (Collection<Component>) componentRepository.findAll();
	}
	
	public Component findComponent(String name) {
		
		String componentName = name.trim().toLowerCase();
		
		return componentRepository.findByName(componentName);
	}
	
	public Component createComponent(final Component c) throws ComponentExistsException {
		Assert.notNull(c,"Component cannot be null");
		
		String componentName = c.getName().trim().toLowerCase();
		Set<Role> roles		 = c.getRoles();
		
		Component _c = componentRepository.findByName(c.getName());
		
		if(null != _c) 
			throw new ComponentExistsException("Component : " + componentName + " exists");
		
		Component newc = new Component();
		newc.setName(componentName);
		newc.setRoles(roles);
		
		return componentRepository.save(newc);
	}
	
	public Component updateComponent(final Integer id , final Component c) 
			throws ComponentNotFoundException {
		
		Assert.notNull(id, "component id cannot be null");
		Assert.notNull(c, "component cannot be null");
		
		Component _c = componentRepository.findOne(id);
		
				
		if(null == c)
			throw new ComponentNotFoundException("Component with id : " + id + "does not exist" );
		
		String componentName = c.getName().trim().toLowerCase();
		_c.setName(componentName);
		return componentRepository.save(_c);
		
	}
	
	public void deleteComponent(Integer id) throws ComponentNotFoundException {
		Assert.notNull(id,"id cannot be null");
		
		Component _c = componentRepository.findOne(id);
		
		if(null == _c) {
			throw new  ComponentNotFoundException("Component with id : " + id + "does not exist" );
		}
		componentRepository.delete(_c.getId());
	}
	
}
