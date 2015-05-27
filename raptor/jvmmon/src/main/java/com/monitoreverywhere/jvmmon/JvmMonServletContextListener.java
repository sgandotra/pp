package com.monitoreverywhere.jvmmon;

import com.monitoreverywhere.jvmmon.mq.JvmMessageProducerImpl;
import com.monitoreverywhere.jvmmon.stats.Collector;


import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class JvmMonServletContextListener implements ServletContextListener, 
    ServletContextAttributeListener {

    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       System.err.println("[L&P Graphite Monitor] :: Shutting down monitoring");
       
       try {
           Collector.getInstance().shutdown();
           JvmMessageProducerImpl.getInstance().shutdown();
       } catch (Throwable t) {
           System.err.println("Error in shutting down monitor : "+t.getMessage());
           System.err.println("Error : "+t.getStackTrace());
       }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.err.println("[L&P Graphite Monitor] :: Start monitoring");
        
        try {
            Collector.getInstance().run();
            JvmMessageProducerImpl.getInstance().run();
        } catch (Throwable t) {          
            System.err.println("Internal Error : "+t.getMessage());               
            System.err.println("Error : "+t.getStackTrace());
        }
        
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        
        
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        
        
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        
        
    }

  
    



}
