package com.ptaas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.repository.SystemConfiguration;
import com.ptaas.repository.SystemConfigurationRepository;

@Service("systemConfigurationSvc")
public class SystemConfigurationSvc {

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    public void toggleInUse(SystemConfiguration systemConfiguration, boolean flag) {
       Assert.notNull(systemConfiguration,"System Configuration cannot be null");
       
       SystemConfiguration sysConfig = systemConfigurationRepository.findOne(systemConfiguration.getId());
       sysConfig.setRunning(flag);
       systemConfigurationRepository.save(sysConfig);
        
    }
    
    
    
}
