package com.ptaas.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("jmeterConsoleDataRepository")
public interface JmeterConsoleDataRepository extends CrudRepository<JmeterConsoleData,Integer>{

    public JmeterConsoleData findByExecutionid(int executionid);
}
