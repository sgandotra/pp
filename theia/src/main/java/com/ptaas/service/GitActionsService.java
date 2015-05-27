package com.ptaas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ptaas.exception.GitCloneException;
import com.ptaas.model.GitListResponse;
import com.ptaas.repository.LoadGenerator;
import com.ptaas.service.MonitoringSvc.MyUrlConnectionClient;
import com.ptaas.service.exception.LoadGeneratorUnavailableException;

@Service("gitActionsService")
public class GitActionsService {
    
    private static final String HOSTMON_PORT = "3000";
    
    private static final Logger logger = LoggerFactory.getLogger(GitActionsService.class);

    @Autowired
    private LoadGeneratorSvc loadGeneratorSvc;
    
    public void gitClone(String hostName) throws LoadGeneratorUnavailableException, GitCloneException {
        Assert.hasText(hostName, "Host name cannot be null");
        
        //validate if the host is a load generator and up and running
        LoadGenerator loadGenerator = loadGeneratorSvc.getAvailability(hostName);
        
        if(null == loadGenerator || !loadGenerator.getStatus() || loadGenerator.getInuse())
            throw new LoadGeneratorUnavailableException("Either the hostname is an invalid/unavailable load generator or is"
                    + " is being used by someone else");
        
        //do a git clone
        HostMonService hostMonService = createClient(loadGenerator.getLgname());
        GitCloneResponse gitClone     = hostMonService.gitClone();
        
        if(null == gitClone || !gitClone.getResponse().equalsIgnoreCase("SUCCESS"))
            throw new GitCloneException("Error in doing a git clone, please contact PTAAS Team");
        
        logger.info("Git clone successful on host : {}",hostName);  
        
    }
    

    
    public GitListResponse list(String hostName) throws LoadGeneratorUnavailableException {
        Assert.hasText(hostName,"hostName cannot be null");
        
        //validate if the host is a load generator and up and running
        LoadGenerator loadGenerator = loadGeneratorSvc.getAvailability(hostName);
        
        if(null == loadGenerator || !loadGenerator.getStatus() || loadGenerator.getInuse())
            throw new LoadGeneratorUnavailableException("Either the hostname is an invalid/unavailable load generator or is"
                    + " is being used by someone else");
        
        
        //do a git list
        HostMonService hostMonService = createClient(loadGenerator.getLgname());
        GitListResponse gitList       = hostMonService.gitList();
        
        return gitList;
    }
    
    
    private HostMonService createClient(String hostName) {
        
        Gson gson = new GsonBuilder()
        .setDateFormat("yyyy/MM/dd")
        .create();
        
        
        RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint("http://"+ hostName + ":" +HOSTMON_PORT)
        .setLogLevel(LogLevel.FULL)
        .setClient(new MyUrlConnectionClient())
        .setConverter(new GsonConverter(gson))
        .build();
    
        return restAdapter.create(HostMonService.class);
    }


}
