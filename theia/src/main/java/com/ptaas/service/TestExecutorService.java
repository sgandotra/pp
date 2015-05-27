package com.ptaas.service;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;

import com.ptaas.model.JmeterGetTestConfig;
import com.ptaas.model.JmeterRequestAction;
import com.ptaas.model.JmeterResponseAction;
import com.ptaas.model.JmeterStopResponse;
import com.ptaas.model.MonitoredHostsModel;
import com.ptaas.model.TestExecutionStartRequest;
import com.ptaas.model.TestExecutionStartResponse;
import com.ptaas.model.TestExecutionStopResponse;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.model.TestExecutionStartResponse.Status;
import com.ptaas.repository.JmeterConsoleData;
import com.ptaas.repository.JmeterConsoleDataRepository;
import com.ptaas.repository.LoadGenerator;
import com.ptaas.repository.StageMachine;
import com.ptaas.repository.SystemConfiguration;
import com.ptaas.repository.TestResult;
import com.ptaas.repository.TestResultRepository;
import com.ptaas.repository.User;
import com.ptaas.repository.UserRepository;
import com.ptaas.service.MonitoringSvc.Action;
import com.ptaas.service.MonitoringSvc.MyUrlConnectionClient;
import com.ptaas.service.exception.InvalidJmeterParamException;
import com.ptaas.service.exception.InvalidRequestException;
import com.ptaas.service.exception.InvalidTidException;
import com.ptaas.service.exception.JmeterStopException;
import com.ptaas.service.exception.JmeterTestExecutionException;
import com.ptaas.service.exception.LoadGeneratorUnavailableException;
import com.ptaas.service.exception.StageMachineUnavailableException;
import com.ptaas.service.exception.SystemConfigNotFoundException;
import com.ptaas.service.exception.UserNotFoundException;


@Service("testExecutorService")
public class TestExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(TestExecutorService.class);
    
    @Value("${jmeter.arg.prefix}")
    private String VALID_JMETER_ARG_PREFIX;
    
    @Value("${jmeter.arg.prefix.vusers}")
    private String VUSERS_PREFIX;
    
    @Value("${jmeter.arg.prefix.duration}")
    private String DURATION_PREFIX;
    
    @Value("${jmeter.arg.prefix.stagename}")
    private String STAGENAME_PREFIX;
    
    @Value("${hostmon.port}")
    private String HOSTMON_PORT ;
    
    
    @Autowired
    private LoadGeneratorSvc loadGeneratorSvc;
    
    @Autowired
    private StageMachineSvc stageMachineSvc;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private TestResultSvc testResultSvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MonitoringStatusSvc monitoringStatusSvc;
    
    @Autowired
    private MonitoringToggleSvc monitoringToggleSvc;
    
    @Autowired
    private JmeterConsoleDataRepository jmeterConsoleDataRepository;
    

    @Transactional(rollbackFor=Exception.class)
    public TestExecutionStartResponse start(TestExecutionStartRequest request) {
        
        TestExecutionStartResponse teResponse = new TestExecutionStartResponse();
        TestResult testResult                 = new TestResult();
        
        try {
        
            logger.info("[1] Validating request params");
            
            //validate parameters
            validateParams(request);
        
            logger.info("[2] Verifying user corp id");
            User user = validateCorpId(request.getCorpId());
            
            //check stagemachine status
            logger.info("[3] Checking SUT availability");
            StageMachine stageMachine = checkSutAvailablity(request.getSut().getStageName());
        
            //check load generator status
            logger.info("[4] Checking load generator availablitity");
            LoadGenerator lg          = checkLgAvailability(request.getJmeter().getMachineName());
            
            //if system configuration is given validate and trigger monitoring
            logger.info("[5] activating provided system configuration");
            toggleSystemConfiguration(request.getSystemConfiguration(),request.getJmeter().getConfig().getDuration(),Action.ACTIVATE);
         
            //start jmeter
            logger.info("[6] Start Jmeter");
            JmeterResponseAction response = startJmeter(request);
            
            //update database
            logger.info("[7] update database");
            testResult = updateStatus(request,response,stageMachine,lg,user);
        
            
            
            teResponse.getResponse().setStatus(Status.SUCCESS);
            teResponse.getResponse().setMessage("");
        } catch(Exception ex) {
            logger.error("Error while configuring and starting jmeter : {}", ex);
            teResponse.getResponse().setStatus(Status.FAILURE);           
            teResponse.getResponse().setMessage(ex.getMessage());           
        }
        teResponse.setTestId(testResult.getTid());
        teResponse.setLoadgenerator(request.getJmeter().getMachineName());
        teResponse.setSut(request.getSut().getStageName());
        teResponse.setConfig(request.getJmeter().getConfig());
        
        return teResponse;
    }
   
    @Transactional(rollbackFor=Exception.class)
    public TestExecutionStopResponse stop(int tid) {
       
       logger.info("Deleting tid : {}",tid);
       TestExecutionStopResponse teResponse = new TestExecutionStopResponse();
       
       try {
           //validate tid
           TestResult testResult = testResultSvc.getTid(tid);
           logger.info("[1] Found valid testResult with tid : {}",tid);
           
           //stop jmeter           
           stopJmeter(testResult.getExecutionId(),testResult.getLoadgenerator().getLgname());
           logger.info("[2] jmeter instance stopped successfully");
           
           //stop system configuration
           logger.info("[3] deactivating system configuration");
           toggleSystemConfiguration(testResult.getSystemConfiguration().getName(),0L,Action.DEACTIVATE);
           
           //update test result from running to completed
           testResultSvc.stop(testResult);
           logger.info("[4] updated testResults with status");
           
           //toggle flag for lg and stage machine
           stageMachineSvc.toggleInUse(testResult.getStagemachine(), false);           
           loadGeneratorSvc.toggleInUse(testResult.getLoadgenerator(), false);
           logger.info(" Released resources back to the pool");
           
           teResponse.getResponse().setStatus(Status.SUCCESS);
           teResponse.getResponse().setMessage("");  
       } catch(Exception ex) {
           teResponse.getResponse().setStatus(Status.FAILURE);
           teResponse.getResponse().setMessage(ex.getMessage());  
       }
       
       teResponse.setTestId(tid);
       teResponse.setTimestamp(new Date());
       
       return teResponse;
       
   }
    
    public TestResult status(int tid) throws InvalidTidException {
       
       return testResultSvc.getTid(tid);
        
    } 
    
    public Page<TestResult> getAll(Principal user,TestResultRepository.Status status,Pageable pageable) {
        Assert.notNull(user,"user cannot be null");
        
        String userName = user.getName();
        return testResultSvc.getAllByUserAndStatus(userName, status,pageable);
                       
    }
    
    public  JmeterGetTestConfig getConfig(int tid) throws InvalidTidException, SystemConfigNotFoundException {
        
        TestResult testResult = testResultSvc.getTid(tid);
        
        JmeterGetTestConfig.JmeterConfig jmeterConfig = new JmeterGetTestConfig.JmeterConfig();
        jmeterConfig.setStartTime(testResult.getDateCreated().getTime());
        jmeterConfig.setDuration(testResult.getDuration());
        jmeterConfig.setVusers(testResult.getVusers());
        
        MonitoredHostsModel monitoredHostsModel = 
                monitoringStatusSvc.getStatusByConfig(testResult.getSystemConfiguration().getName());
        JmeterGetTestConfig jmeterTestConfig = new JmeterGetTestConfig();
        jmeterTestConfig.setJmeterConfig(jmeterConfig);
        jmeterTestConfig.setMonitoredHostsModel(monitoredHostsModel);
        
        
        
        return jmeterTestConfig;
    }
    
    public JmeterConsoleData getConsoleData(int tid) throws InvalidTidException {
        int executionId =  testResultSvc.getTid(tid).getExecutionId();
        
        return jmeterConsoleDataRepository.findByExecutionid(executionId);
    }
   
   private void stopJmeter(int executionId, String hostName) throws JmeterStopException {
       
       Assert.notNull(hostName, "hostName cannot be null");
       
       logger.info("Stopping jmeter with execution id {}",executionId);
       HostMonService svc = createClient(hostName);
       JmeterStopResponse response = svc.stopJmeter(executionId);
       
       if(!response.getResponse().equals(JmeterStopResponse.Status.SUCCESS)) 
           throw new JmeterStopException("unable to stop jmeter execution on the client : " + hostName);
       
       if(response.getMsg().equals("pid not found")) 
           throw new JmeterStopException("could not find a jmeter instance to stop on " + hostName);
       
       logger.info("Jmeter instance successfully stopped");
       
   }
   
    
    private void validateParams(TestExecutionStartRequest request) throws InvalidJmeterParamException {
        
        String params = request.getJmeter().getConfig().getParams();
        
        if(null != params) {
            String[] args = params.split("\\s+");
            if(null == args || args.length == 0)
                throw new InvalidJmeterParamException("Invalid format of params use -Dparamname=paramvalue");
            
            for(String arg : args) {
                if(!arg.startsWith(VALID_JMETER_ARG_PREFIX) || !arg.contains("="))
                    throw new InvalidJmeterParamException("Invalid format of params use -Dparamname=paramvalue");
            }
        }
        
    }
    
    private User validateCorpId(String corpId) throws UserNotFoundException {
        User user = userRepository.findByUserName(corpId);
        
        if(null == user)
            throw new UserNotFoundException(corpId + " does not exist, login to PTAAS website before trying again");
        
        return user;
    }
    
    private LoadGenerator checkLgAvailability(String hostName) throws LoadGeneratorUnavailableException {
        LoadGenerator lg = loadGeneratorSvc.getAvailability(hostName);
        
        if(null == lg || lg.getInuse() || !lg.getStatus())
            throw new LoadGeneratorUnavailableException("Load Generator unavailable or inuse");
        
        return lg;
    }
    
    private StageMachine checkSutAvailablity(String hostName) throws StageMachineUnavailableException {
        StageMachine stageMachine = stageMachineSvc.getAvailability(hostName);
        
        if(null == stageMachine || stageMachine.getInuse() || !stageMachine.getStatus()) {
            throw new StageMachineUnavailableException("Stage Machine unavailable or inuse");
        }
        
        return stageMachine;
    } 
    
    private void toggleSystemConfiguration(String systemConfig,long duration,Action action) 
            throws SystemConfigNotFoundException, InterruptedException, InvalidRequestException {
        
        if(null == systemConfig) {
            logger.info("No system configuration provided to start monitoring");
            return;
        }   
        SystemConfiguration configuration = systemConfigService.findByName(systemConfig);
        
        if(null == configuration) 
            throw new SystemConfigNotFoundException("System configuration : " + systemConfig + " not found");
        
        //activate config
        MonitoredHostsModel model = monitoringStatusSvc.getStatusByConfig(systemConfig);  
        
        if(action.equals(Action.ACTIVATE)) {            
            Map<String,Host> hosts = model.getHosts();
            long nmonStart         = new Date().getTime();
            long nmonEnd           = nmonStart + duration;
            
            //set duration for each host
            for(Host host : hosts.values()) {
                host.setNmon(true);
                host.setNmonStart(nmonStart);
                host.setNmonEnd(nmonEnd);
            }
        }
        
        monitoringToggleSvc.process(model,action);
        
    }
    
    private JmeterResponseAction startJmeter(TestExecutionStartRequest request) throws JmeterTestExecutionException {
        
        String lg = request.getJmeter().getMachineName();
        
        logger.info("Starting a new jmeter execution on lg machine : {}",lg);
        JmeterRequestAction action = new JmeterRequestAction();
        action.setScript(request.getJmeter().getConfig().getScriptName());
        
        JmeterRequestAction.Config config = new JmeterRequestAction.Config();
        config.setDuration(DURATION_PREFIX+String.valueOf(request.getJmeter().getConfig().getDuration()));
        config.setStageName(STAGENAME_PREFIX+request.getSut().getStageName());
        config.setVusers(VUSERS_PREFIX+String.valueOf(request.getJmeter().getConfig().getVusers()));
        config.setParams(request.getJmeter().getConfig().getParams());
        
        action.setConfig(config);
        
        HostMonService hostMonService   = createClient(lg);
        try {
            JmeterResponseAction response = hostMonService.activateJmeter(action);
        
            if(!response.getResponseMsg().equals("SUCCESS"))
                throw new JmeterTestExecutionException("Could not start Jmeter test execution on host :"+lg);
            return response;
        } catch (RetrofitError error){
            throw new JmeterTestExecutionException("Could not start Jmeter test execution on host :"+lg + " failed with error : "+error.getMessage());
        }
        
       
    }
    
    private TestResult updateStatus(TestExecutionStartRequest request , JmeterResponseAction response,
            StageMachine stageMachine, LoadGenerator lg, User user) {
        Assert.notNull(response,"Jmeter response cannot be null");
        Assert.isTrue(response.getResponseMsg().equals("SUCCESS"), " jmeter service response should be success");
        
        logger.info("Writing test results for lg {} and stagemachine {}",lg,stageMachine);
        
        SystemConfiguration systemConfiguration = systemConfigService.findByName(request.getSystemConfiguration());
        
        TestResult testResult = new TestResult();
        testResult.setCommandlineparams(request.getJmeter().getConfig().getParams());
        testResult.setDuration(request.getJmeter().getConfig().getDuration());
        testResult.setDateCreated(new Date(response.getDateStarted()));
        testResult.setLoadgenerator(lg);
        testResult.setStagemachine(stageMachine);
        testResult.setVusers(request.getJmeter().getConfig().getVusers());
        testResult.setExecutionId(Integer.valueOf(response.getExecutionID()));
        testResult.setDescription(request.getDescription());
        testResult.setUser(user);
        testResult.setSystemConfiguration(systemConfiguration);

        if(response.getResponseMsg().equals("SUCCESS"))
            testResult.setStatus("RUNNING");
        
        testResult = testResultSvc.save(testResult);
        
        stageMachineSvc.toggleInUse(stageMachine, true);
        
        loadGeneratorSvc.toggleInUse(lg, true);
        
        return testResult;
        
    }
    
    
    private HostMonService createClient(String hostName) {
        RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint("http://"+ hostName + ":" +HOSTMON_PORT)
        .setLogLevel(LogLevel.FULL)
        .setClient(new MyUrlConnectionClient())
        .build();
    
        return restAdapter.create(HostMonService.class);
    }

   

   
    
}
