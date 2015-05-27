package com.ptaas.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageMachineRepository extends CrudRepository<StageMachine,Integer> {
	
	public StageMachine findByStagename(String stageName);
	
	public Collection<StageMachine> findByStatusAndInuse(boolean status, boolean inuse);
	
	public Collection<StageMachine> findByStatus(boolean status);
	
	public Collection<StageMachine> findByInuse(boolean inuse);


}
