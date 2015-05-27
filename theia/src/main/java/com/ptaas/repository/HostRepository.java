package com.ptaas.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostRepository extends CrudRepository<Host,Integer>{

	public Host findByHostName(String name);
}
