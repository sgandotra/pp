package com.ptaas.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.model.RoleModel;
import com.ptaas.repository.Component;
import com.ptaas.repository.ComponentRepository;
import com.ptaas.repository.Role;
import com.ptaas.repository.RoleRepository;
import com.ptaas.service.exception.ComponentNotFoundException;
import com.ptaas.service.exception.InvalidRequestException;
import com.ptaas.service.exception.RoleExistsException;
import com.ptaas.service.exception.RoleNotFoundException;

@Service("roleService")
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private ComponentRepository componentRepository;
	
	public Collection<Role> findAll() {
	
		return (Collection<Role>) roleRepository.findAll();
	}
	
	public Role findByName(String name) {
		Assert.notNull(name,"role name cannot be null");
		
		String roleName = name.toLowerCase().trim();
		
		return roleRepository.findByName(roleName);
		
	}
	
	public Role createRole(RoleModel roleModel) throws RoleExistsException {
		
		Assert.notNull(roleModel,"role Model cannot be null");
		
		String roleName = roleModel.getRoleName().trim().toLowerCase();
		
		Role existingRole = roleRepository.findByName(roleName);	
		if(null != existingRole)
			throw new RoleExistsException("role with name :" + roleName + " exists");
		
		Role r = new Role();
		r.setName(roleName);
		
		Set<Component> components = new HashSet<Component>(roleModel.getComponents().size());
		for(String name : roleModel.getComponents()) {
			Component c = componentRepository.findByName(name);
			components.add(c);
		}
		r.setComponents(components);
		return roleRepository.save(r);
		
		
	}
	
	public Role updateRole(Integer id, RoleModel roleModel) throws RoleNotFoundException {
		
		Assert.notNull(id, "id cannot be null");
		Assert.notNull(roleModel, " role model cannnot be null");
		
		Role r = roleRepository.findOne(id);
		
		if(null == r)
			throw new RoleNotFoundException( " role with id : " + id + " not found");
		
		Set<Component> components = new HashSet<Component>(roleModel.getComponents().size());
		for(String cName : roleModel.getComponents()) {
			Component c = componentRepository.findByName(cName);
			components.add(c);
		}
		r.setComponents(components);
		return roleRepository.save(r);
	}
	
	public void deleteRole(Integer id) throws RoleNotFoundException {
		Assert.notNull(id, " id cannot be null");
		
		Role r = roleRepository.findOne(id);
		
		if(null == r) 
			throw new RoleNotFoundException( " role with id : " + id + " not found");
		
		roleRepository.delete(r.getId());
	}
	
	public void deleteComponentsFromRole(Integer id, String name) 
			throws RoleNotFoundException, ComponentNotFoundException, InvalidRequestException {
		
		Assert.notNull(id, "Role id cannot be null");
		Assert.notNull(name,"component name cannot be null");
		
		Role r = roleRepository.findOne(id);
		if(null == r)
			throw new RoleNotFoundException(" no role found for id : " + id);
		
		if(r.getComponents().size() == 1)
			throw new InvalidRequestException("Atleast one component is required with a role");
		
		
		String componentName = name.trim().toLowerCase();
		Component c = componentRepository.findByName(name);
		if(null == c) 
			throw new ComponentNotFoundException("no component with name : " + componentName);
		
		r.getComponents().remove(c);
		roleRepository.save(r);
		
	}
	
}
