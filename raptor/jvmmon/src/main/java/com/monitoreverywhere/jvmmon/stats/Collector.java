package com.monitoreverywhere.jvmmon.stats;


import com.monitoreverywhere.jvmmon.model.Stats;
import com.monitoreverywhere.jvmmon.model.Stats.Builder;
import com.monitoreverywhere.jvmmon.mq.JvmMessageProducer;
import com.monitoreverywhere.jvmmon.mq.JvmMessageProducerImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class Collector {

    private static final Collector c        = new Collector();
    private static final long INITIAL_DELAY = 10000L;
    private static final long PERIOD        = 10000L;
        
    private final ScheduledExecutorService service;
    
    private AtomicBoolean isRunning         = new AtomicBoolean(false);
    
    private Collector() {        
        service = Executors.newSingleThreadScheduledExecutor();        
    }
    
    public static Collector getInstance() {
        return c;
    }
    
    
    public void run() {
        if (isRunning.compareAndSet(false, true)) {            
            System.err.println(String.format("Starting scheduler with initial delay [%s] and period [%s]",
                    INITIAL_DELAY,PERIOD));
            
            service.scheduleAtFixedRate(new Runner(), 
                    INITIAL_DELAY, 
                    PERIOD, 
                    TimeUnit.MILLISECONDS);                     
        } else {
            System.err.println("An instance of collector already running");
        }
            
    }
    
    public void shutdown() {
        service.shutdown();
        
        try {
            if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                service.shutdownNow();              
              if (!service.awaitTermination(10, TimeUnit.SECONDS))
                  System.err.println("service pool did not terminate");
            }
          } catch (InterruptedException ie) {
              service.shutdownNow();
              Thread.currentThread().interrupt();
          }
    }
    
    public boolean isRunning() {
        return isRunning.get();
    }
    
    static final class Runner implements Runnable {

        private final JvmMessageProducer jvmMessageProducer;
        
        Runner() {
            jvmMessageProducer  = JvmMessageProducerImpl.getInstance();
        }
        
        @Override
        public void run() {
            try {
            Builder builder = new Stats.Builder();
            builder.setClassLoaderStats(new ClassLoaderStatCollector().getStats());
            builder.setGarbageCollector(new GarbageStatCollector().getStats());
            builder.setMemoryPoolCollector(new MemoryPoolStatCollector().getStats());
            builder.setMemoryStatCollector(new MemoryStatCollector().getStats());
            builder.setRunTimeCollector(new RunTimeStatCollector().getStats());
            
            
            Stats stats = builder.build();
            jvmMessageProducer.getQueue().offer(stats); 
            } catch(Exception ex) {
            	System.err.println("Error : "+ex.getMessage());
            	ex.printStackTrace();
            }
        }
        
    }
                    
}
    
    

