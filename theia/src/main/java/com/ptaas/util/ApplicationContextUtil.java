package com.ptaas.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextUtil implements ApplicationContextAware {
	
	 private static ApplicationContextUtil instance;
	 private static PropertyUtil propertyUtilInstance;
     
	    private ApplicationContext applicationContext;
	     
	    private static synchronized ApplicationContextUtil getInstance(){
	         
	        if(instance == null){
	            instance = new ApplicationContextUtil();
	            propertyUtilInstance=PropertyUtil.getInstance(ApplicationContextUtil.class);
	        }
	         
	        return instance;
	    }
	     
	    @Override
	    public void setApplicationContext(ApplicationContext applicationContext)
	            throws BeansException {
	        if(getInstance().applicationContext == null){
	            getInstance().applicationContext = applicationContext;
	        }
	         
	    }
	 
	    public static ApplicationContext getApplicationContext(){
	        return getInstance().applicationContext;
	    }

		public static PropertyUtil getPropertyUtilInstance() {
			return propertyUtilInstance;
		}
	    
	
}
