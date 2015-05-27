package com.ptaas.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.model.HostModel;
import com.ptaas.repository.Host;
import com.ptaas.repository.HostRepository;
import com.ptaas.repository.Role;
import com.ptaas.repository.RoleRepository;
import com.ptaas.repository.SystemConfigurationRepository;
import com.ptaas.service.exception.HostExistsException;
import com.ptaas.service.exception.HostNotFoundException;

@Service("hostService")
public class HostService {

	private static final String UNASSIGNED = "unassigned";
	
	@Autowired
	private HostRepository hostRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;
	
	@Autowired
	private ScheduledStatusService scheduedService;
	
	public Collection<Host> findAll() {
		return (Collection<Host>) hostRepository.findAll();
	}

	public Host get(Integer id) {
		Assert.notNull(id, " id cannot be null");
		
		return hostRepository.findOne(id);
	}
	
	public Host findByHostName(String name) {
		Assert.notNull(name , "host name cannot be null");
		
		String hostName = name.trim().toLowerCase();
		
		return hostRepository.findByHostName(hostName);
	}
	
	public Host createHost(HostModel hostModel) throws HostExistsException {
		Assert.notNull(hostModel, " hostModel cannot be null");
		
		String hostName = hostModel.getName().trim().toLowerCase();
		Host host 		= hostRepository.findByHostName(hostName);
		
		if(null != host) 
			throw new HostExistsException("host with " + hostName + " already exists");
		
		Set<Role> roles = new HashSet<Role>(hostModel.getRoles().size());
		for(String name : hostModel.getRoles()) {
			Role r = roleRepository.findByName(name);
			if(null != r)
				roles.add(r);
		}
		
		Host h = new Host();
		h.setSystemConfiguration(systemConfigurationRepository.findByName(UNASSIGNED));
		h.setHostName(hostName);
		h.setRoles(roles);
		h = hostRepository.save(h);
		scheduedService.getHostInfo(h.getId());
		
		return h;
	}
	
	public Host updateHost(Integer id, HostModel hostModel) throws HostNotFoundException {
		Assert.notNull(id, " id cannot be null");
		Assert.notNull(hostModel, " hostmodel cannot be null");
		
		Host h = hostRepository.findOne(id);
		
		if(null == h)
			throw new HostNotFoundException(" host with id  : " + id + " not found");
		
		String hostName = hostModel.getName().trim().toLowerCase();
		
		Set<Role> roles = new HashSet<Role>(hostModel.getRoles().size());
		for(String rName : hostModel.getRoles()) {
			Role r = roleRepository.findByName(rName);
			if(null != r)
				roles.add(r);
		}
		
		h.setRoles(roles);
		h.setHostName(hostName);
		return hostRepository.save(h);
	}
	
	
	public void deleteHost(Integer id) throws HostNotFoundException {
		
		Assert.notNull(id, " host id cannot be null");
		
		Host h = hostRepository.findOne(id);
		if(null == h) 
			throw new HostNotFoundException (" host with id " + id + " not found");
		
		
		hostRepository.delete(h.getId());
				
	}
	
	public void updateNmon(String hostName,long nmonstart,long nmonend) throws HostNotFoundException {
		Assert.notNull(hostName, "host name cannot be null");
		
		Host h  = hostRepository.findByHostName(hostName);
		if(null == h) 
			throw new HostNotFoundException(" host with hostname : " + hostName + " not found");
	
		h.setNmonend(nmonend);
		h.setNmonstart(nmonstart);
		
		hostRepository.save(h);
	
	}
	
}
