package com.ptaas.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptaas.model.JmeterStatusResponse;
import com.ptaas.repository.JmeterConsoleData;
import com.ptaas.repository.JmeterConsoleDataRepository;
import com.ptaas.repository.TestResult;

@Service("jmeterConsoleDataSvc")
public class JmeterConsoleDataSvc {

    @Autowired
    private JmeterConsoleDataRepository jmeterConsoleDataRepository;
    
    private static final Logger logger = LoggerFactory.getLogger("jmeterConsoleDataSvc");

    public void save(TestResult result,JmeterStatusResponse response) {
                
        JmeterConsoleData entity = jmeterConsoleDataRepository.findByExecutionid(result.getExecutionId());
        
        if(null == entity) {
            entity = new JmeterConsoleData();
            entity.setTestResult(result);
        }
        
        entity.setDateModified(new Date());
        
        JmeterStatusResponse.Data.Status status = response.getData().getStatus();
        
        entity.setErrors((int)status.getErrors());
        entity.setLatency((int)status.getLatency());
        entity.setResponsetime((int) status.getRT());
        
        String samples = status.getSamples().substring(1);
        entity.setSamples(Integer.valueOf(samples));
        
        String[] threads    = status.getThreads().split("/");
        int totalThreads    =  Integer.valueOf(threads[0]);
        int runningThreads  = Integer.valueOf(threads[1]);
        
        entity.setTotalthreads(totalThreads);
        entity.setRunningthreads(runningThreads);
        
        entity.setTestResult(result);
        entity.setExecutionid(result.getExecutionId());
        
        logger.info("Saving : {}",entity);
        
        
        jmeterConsoleDataRepository.save(entity);
 
    }
    
    public JmeterConsoleData get(int executionid) {
        
        return jmeterConsoleDataRepository.findByExecutionid(executionid);
        
        
    }
    
}
