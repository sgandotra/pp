package com.ptaas.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ptaas.model.JmeterGetTestConfig;
import com.ptaas.model.TestExecutionStartRequest;
import com.ptaas.model.TestExecutionStartResponse;
import com.ptaas.model.TestExecutionStopResponse;
import com.ptaas.model.TestExecutionStartResponse.Status;
import com.ptaas.repository.JmeterConsoleData;
import com.ptaas.repository.TestResult;
import com.ptaas.repository.TestResultRepository;
import com.ptaas.service.TestExecutorService;
import com.ptaas.service.exception.InvalidTidException;
import com.ptaas.service.exception.SystemConfigNotFoundException;



@Controller
@RequestMapping("/jmeter")
public class TestExecutionController {

	private static final Logger logger = LoggerFactory.getLogger(TestExecutionController.class);
	
	@Autowired
	private TestExecutorService testExecutorService;
	
	@RequestMapping(method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TestExecutionStartResponse> run(@NotNull @Valid @RequestBody TestExecutionStartRequest testExecutionRequest) {
	    
	    logger.info("Received request to execute Jmeter :"+testExecutionRequest);
	    TestExecutionStartResponse response = null;
	    //TestExecutionStartResponse response = testExecutorService.start(testExecutionRequest);
	    
	    /**
	     * 
	     * for testing only
	     * 
	     
	    try {
	        FileOutputStream fos   = new FileOutputStream("/tmp/tesr.out");
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
	        oos.writeObject(response);
	        oos.close();
	        fos.close();
	    } catch(Exception ex) {
	        ex.printStackTrace();
	    }*/
	    
	    try {
	        FileInputStream fis = new FileInputStream("/tmp/tesr.out");
	        ObjectInputStream ois = new ObjectInputStream(fis);
	        response = (TestExecutionStartResponse) ois.readObject();
	        ois.close();
	        fis.close();
	    } catch(Exception ex) {
	        ex.printStackTrace();
	    }
	    
	    if(response.getResponse().getStatus() == Status.SUCCESS)	        
	        return new ResponseEntity<TestExecutionStartResponse>(response, HttpStatus.CREATED);
	    else
	        return new ResponseEntity<TestExecutionStartResponse>(response,HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/{tid}", method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TestExecutionStopResponse> stop(@NotNull @PathVariable int tid) {
		logger.info("Received a request to stop Jmeter execution for tid {} ",tid);
		
		TestExecutionStopResponse response = testExecutorService.stop(tid);
		
		if(response.getResponse().getStatus() == Status.SUCCESS)          
            return new ResponseEntity<TestExecutionStopResponse>(response, HttpStatus.OK);
        else
            return new ResponseEntity<TestExecutionStopResponse>(response,HttpStatus.BAD_REQUEST);
		
	}
	
	@RequestMapping(value="/{tid}", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TestResult status(@NotNull @PathVariable int tid) throws InvalidTidException {
	    logger.info("Received request to find status of tid : {}",tid);
		return testExecutorService.status(tid);
	}
	
	@RequestMapping(value="/all/{status}", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<TestResult> getAllByUserAndStatus(Principal user,
            @NotNull @PathVariable TestResultRepository.Status status,
            Pageable pageable) {
        logger.info("Received request to find all tests for user {}",user.getName());
        
        return testExecutorService.getAll(user,status,pageable);
    }
	
	@RequestMapping(value="/config/{tid}", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JmeterGetTestConfig getConfig(Principal user,
            @NotNull @PathVariable int tid) throws InvalidTidException, SystemConfigNotFoundException {
        logger.info("Received request to get config for tid : {}",tid);
        
        return testExecutorService.getConfig(tid);
    }
	
	@RequestMapping(value="/console/data/{tid}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public JmeterConsoleData getConsoleData(Principal user,
            @NotNull @PathVariable int tid) throws InvalidTidException {
        logger.info("Received request to get config for tid : {}",tid);
        
        return testExecutorService.getConsoleData(tid);
    }
	
}
