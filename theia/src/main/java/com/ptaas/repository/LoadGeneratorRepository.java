package com.ptaas.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;


@Repository
public interface LoadGeneratorRepository extends CrudRepository<LoadGenerator,Integer>{
	
	public Collection<LoadGenerator> findByStatusAndInuse(boolean status, boolean inuse);
	
	public LoadGenerator findByLgname(String name);

}
