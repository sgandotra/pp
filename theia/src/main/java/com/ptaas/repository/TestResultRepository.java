package com.ptaas.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository("testResultRepository")
public interface TestResultRepository extends PagingAndSortingRepository<TestResult,Integer> {
    
    public enum Status {
        COMPLETED,
        RUNNING,
        STOPPED,
        SHARED
    }

	public Collection<TestResult> findByLoadgenerator(LoadGenerator loadGenerator);
	
	public Collection<TestResult> findByStagemachine(StageMachine stageMachine);
	
	public Collection<TestResult> findByStatus(String status);
	
	public Page<TestResult> findByUserAndStatusOrderByDateCreatedDesc(User user, String status,Pageable pageable);
	
	public Collection<TestResult> findByShared(boolean shared);
	
}
