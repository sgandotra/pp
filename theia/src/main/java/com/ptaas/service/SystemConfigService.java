package com.ptaas.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.model.SystemConfigurationModel;
import com.ptaas.repository.Host;
import com.ptaas.repository.HostRepository;
import com.ptaas.repository.SystemConfiguration;
import com.ptaas.repository.SystemConfigurationRepository;
import com.ptaas.repository.User;
import com.ptaas.repository.UserRepository;
import com.ptaas.service.exception.SystemConfigExistsException;
import com.ptaas.service.exception.SystemConfigNotFoundException;

@Service("systemConfigService")
public class SystemConfigService {

	private static final String UNASSIGNED = "unassigned";
	
	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;
	
	@Autowired
	private HostRepository hostRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	public Collection<SystemConfiguration> findAll() {
		
		SystemConfiguration defaultConfig = systemConfigurationRepository.findByName("unassigned");
		
		Collection<SystemConfiguration> all = (Collection<SystemConfiguration>) systemConfigurationRepository.findAll();
		
		all.remove(defaultConfig);
		
		return all;		
	}
	
	
	public SystemConfiguration get(Integer id) {
		Assert.notNull(id, " id cannot be null");
		
		return systemConfigurationRepository.findOne(id);
	}
	
	public SystemConfiguration findByName(String name) {
		Assert.notNull(name , "name cannot be null");
		
		String configName = name.trim().toLowerCase();
		
		return systemConfigurationRepository.findByName(configName);
		
		
	}
	
	@Transactional
	public SystemConfiguration createSystemConfig(SystemConfigurationModel model) 
			throws SystemConfigExistsException {
		
		Assert.notNull(model," Systemconfigurationmodel cannot be null");
		
		String configName 		 = model.getName().trim().toLowerCase();
		Collection<String> hosts = model.getHosts();
		
		if(systemConfigurationRepository.findByName(configName) != null) 
			throw new SystemConfigExistsException(" config name : "+configName + " exists");
		
		Set<Host> set 					  = new HashSet<Host>(hosts.size());
		SystemConfiguration configuration = new SystemConfiguration();
		
		for(String hostName : hosts) {
			
			Host host = hostRepository.findByHostName(hostName);
			
			if(null != host) {
				set.add(host);
				host.setSystemConfiguration(configuration);
			}
		}
		
		configuration.setName(configName);
		configuration.setHosts(set);
		
		return systemConfigurationRepository.save(configuration);
		
	}
	
	@Transactional
	public SystemConfiguration updateSystemConfig(Integer id, SystemConfigurationModel model) 
			throws SystemConfigNotFoundException {
		
		Assert.notNull(id," id cannot be null");
		Assert.notNull(model, " Systemconfigurationmodel cannot be null");
		
		SystemConfiguration configuration = systemConfigurationRepository.findOne(id);
		
		if(null == configuration) 
			throw new SystemConfigNotFoundException(" id : "+ id + "does not exist");
		
		Collection<String> hosts = model.getHosts();
		Set<Host> set 			 = new HashSet<Host>(hosts.size());
		
		for(String hostName : hosts) {
			
			Host host = hostRepository.findByHostName(hostName);		
			if(null != host) {
				set.add(host);
			}
		}
		configuration.setHosts(set);
		
		return systemConfigurationRepository.save(configuration);
		
	}
	
	@Transactional
	public void deleteSysConfig(Integer id) throws SystemConfigNotFoundException {
		Assert.notNull(id , " id cannot be null");
		
		SystemConfiguration configuration = systemConfigurationRepository.findOne(id);
		
		if(null == configuration) 
			throw new SystemConfigNotFoundException(" id : "+ id + "does not exist");
		
		Set<Host> hosts 				  = configuration.getHosts();
		SystemConfiguration defaultConfig = systemConfigurationRepository.findByName(UNASSIGNED);

		for(Host host : hosts) {
			host.setSystemConfiguration(defaultConfig);
		}
		hostRepository.save(hosts);
		
		systemConfigurationRepository.delete(id);

	}
	
	@Transactional
	public void changeRunningStatus(String name, boolean b) throws SystemConfigNotFoundException {
		Assert.notNull(name , "name cannot be null");
		
		String configName = name.trim().toLowerCase();
		
		SystemConfiguration configuration = systemConfigurationRepository.findByName(configName);
		
		if(null == configuration) 
			throw new SystemConfigNotFoundException(" configName : "+ configName + "does not exist");
		
		User user						  = configuration.getUser();
		configuration.setRunning(b);
		configuration.setUser(user);
		
		configuration = systemConfigurationRepository.save(configuration);
		
	}


    public Collection<SystemConfiguration> findAllByUser(String userName) {
        Assert.hasText(userName,"username cannot be empty or null");
        
        User user = userRepository.findByUserName(userName);
        
        SystemConfiguration defaultConfig = systemConfigurationRepository.findByName("unassigned");        
        Collection<SystemConfiguration> all = (Collection<SystemConfiguration>) systemConfigurationRepository.findByUser(user);        
        all.remove(defaultConfig);
        
        return all;     
    }
	
}
