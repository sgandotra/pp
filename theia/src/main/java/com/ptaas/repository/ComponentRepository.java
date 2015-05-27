package com.ptaas.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository(value="componentRepository")
public interface ComponentRepository  extends CrudRepository<Component,Integer>{
	
	public static final String COMPONENT_NAME = "name";
	
	public Component findByName(@Param(COMPONENT_NAME) String component);

}
