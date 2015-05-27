package com.ptaas.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends CrudRepository<Role,Integer>{

	
	public Role findByName(String name);
	
}
