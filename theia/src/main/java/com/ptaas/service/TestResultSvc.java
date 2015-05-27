package com.ptaas.service;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ptaas.repository.TestResult;
import com.ptaas.repository.TestResultRepository;
import com.ptaas.repository.User;
import com.ptaas.repository.UserRepository;
import com.ptaas.repository.TestResultRepository.Status;
import com.ptaas.service.exception.InvalidTidException;

@Service("testResultSvc")
public class TestResultSvc {

    @Autowired
    private TestResultRepository testResultRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public TestResult save(TestResult testResult) {
        return testResultRepository.save(testResult);
    }
    
    public Collection<TestResult> getRunning() {
        return testResultRepository.findByStatus(Status.RUNNING.name());
    }
    
    public TestResult getTid(int tid) throws InvalidTidException {
        TestResult testResult = testResultRepository.findOne(tid);
        
        if(null == testResult)
            throw new InvalidTidException("Tid : "+ tid + " not found or invalid");
        
        return testResult;
    }

    public void stop(TestResult testResult) {
        testResult.setStatus(Status.STOPPED.name());
        testResult.setDateCreated(new Date());
        testResultRepository.save(testResult);
    }
    
    public Page<TestResult> getAllByUserAndStatus(String userName, Status status,Pageable pageable) {
        
        User user = userRepository.findByUserName(userName);
               
        return testResultRepository.findByUserAndStatusOrderByDateCreatedDesc(user,status.name(),pageable);
        
        
    }
    
}
