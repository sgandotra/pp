package com.ptaas.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;


public interface SystemConfigurationRepository extends CrudRepository<SystemConfiguration,Integer> {
	
	public SystemConfiguration findByName(String name);

    public Collection<SystemConfiguration> findByUser(User user);

}
