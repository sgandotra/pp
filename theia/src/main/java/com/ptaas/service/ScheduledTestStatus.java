package com.ptaas.service;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.GsonConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ptaas.model.JmeterStatusResponse;
import com.ptaas.model.JmeterStatusResponse.Data;
import com.ptaas.repository.LoadGenerator;
import com.ptaas.repository.StageMachine;
import com.ptaas.repository.TestResult;
import com.ptaas.service.MonitoringSvc.MyUrlConnectionClient;

@Service("scheduleTestStatus")
public class ScheduledTestStatus {

    @Autowired
    private TestResultSvc testResultSvc;
    
    @Autowired
    private LoadGeneratorSvc loadGeneratorSvc;
    
    @Autowired
    private StageMachineSvc stageMachineSvc;
    
    @Autowired
    private JmeterConsoleDataSvc jmeterConsoleDataSvc;
    
    @Autowired
    private SystemConfigurationSvc systemConfigurationSvc;
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTestStatus.class);
    
    private ExecutorService executorService  = Executors.newFixedThreadPool(5);
    
    @Value("${hostmon.port}")
    private String HOSTMON_PORT ;
    
        
    
    @Scheduled(fixedDelay=10000)
    public void publish() {
        
        logger.info("Fetching all tests with status RUNNING");
        Collection<TestResult> testResults = testResultSvc.getRunning();
         
        for(TestResult testResult : testResults) {
            logger.info("Processing testResult with executionID : {}",testResult.getExecutionId());
            executorService.submit(new GetDataAndPublish(testResult));
        }
        
    }
    
    
    
    public class GetDataAndPublish implements Runnable {

        private String loadGenerator;
        private String executionID;
        private TestResult testResult;
        
        public GetDataAndPublish(TestResult testResult) {
            this.testResult    = testResult;
            this.loadGenerator = testResult.getLoadgenerator().getLgname();
            this.executionID   = String.valueOf(testResult.getExecutionId());
        }
        
        @Override
        public void run() {
            HostMonService svc = createClient(loadGenerator);
            try {
                JmeterStatusResponse response = svc.getStatus(executionID);
                
                logger.info("JMeter status response : {}",response);
                if(response.getStatus().equals("SUCCESS")) {
                    Data data = response.getData();
                    
                    if(data.getState().equalsIgnoreCase("completed")) {
                        this.testResult.setStatus("COMPLETED");
                        testResultSvc.save(this.testResult);
                        releaseResources(testResult);
                    }else if(data.getState().equalsIgnoreCase("running")) {
                        jmeterConsoleDataSvc.save(testResult,response);
                    } 
                    
                } else {
                    logger.error("pid not found for loadgenerator {} and executionID {}",loadGenerator,executionID);
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            
        }
        
        @Transactional
        private void releaseResources(TestResult testResult) {
            Assert.notNull(testResult,"TestResult cannot be null");                                   
            
            LoadGenerator loadGenerator = testResult.getLoadgenerator();
            StageMachine stageMachine   = testResult.getStagemachine();
            
            logger.info("Toggling OFF inuse flag for load generator {} and stage machine {}",
                    loadGenerator.getLgname(), stageMachine.getStagename());
            
            loadGeneratorSvc.toggleInUse(loadGenerator, false);
            stageMachineSvc.toggleInUse(stageMachine, false);
            systemConfigurationSvc.toggleInUse(testResult.getSystemConfiguration(),false);
            
        }
        
        private HostMonService createClient(String hostName) {
            
            Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();
            
            RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("http://"+ hostName + ":" +HOSTMON_PORT)
            .setLogLevel(LogLevel.FULL)
            .setConverter(new GsonConverter(gson))
            .setClient(new MyUrlConnectionClient())
            .build();
        
            return restAdapter.create(HostMonService.class);
        }
        
    }
        
        
}

