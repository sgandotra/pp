package com.monitoreverywhere.jvmmon.mq;

import com.google.common.collect.ImmutableMap;
import com.monitoreverywhere.config.ConfigurationReader;
import com.monitoreverywhere.jvmmon.model.Stats;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class JvmMessageProducerImpl implements JvmMessageProducer {
    
    public static final Stats POISON                      = null;
        
    private static final ArrayBlockingQueue<Stats> queue  = new ArrayBlockingQueue<Stats>(1);
        
    private static final JvmMessageProducer jvmProducer   = new JvmMessageProducerImpl();
    
    private AtomicBoolean isRunning                        = new AtomicBoolean(false);    

    private final ConfigurationReader configReader;
    
    private final String graphiteHost;
    private final String graphitePort;
    private final String graphitePrefix;
    
    private Thread thread;
            
    private JvmMessageProducerImpl() {
        
        configReader                     = ConfigurationReader.getInstance();
        ImmutableMap<String, String> map = configReader.getProperties();
        
        
        graphitePort 	= map.get(ConfigurationReader.GRAPHITE_PORT);   
        
        graphiteHost 	= map.get(ConfigurationReader.GRAPHITE_HOSTNAME);             
        
        graphitePrefix 	= map.get(ConfigurationReader.GRAPHITE_PREFIX);
        
        //System.err.println(String.format("Using Graphite Host : %s , with port %s"
        //		+ " and prefix %s",graphiteHost,graphitePort,graphitePrefix));
        
    }
    
    public static JvmMessageProducer getInstance() {
        return jvmProducer;
    }
    
    @Override
    public String getGraphitePort() {
        return this.graphitePort;
    }
    
    @Override
    public ArrayBlockingQueue<Stats> getQueue() {
        return queue;
    }
    
    
    @Override
    public void setup() {
	    queue.poll();
    }
    
    @Override
    public void send(Stats stats)  {

    	try {
    	
	    	Map<String,Double> payload = new HashMap<String,Double>();
	    	
	    	addAll(payload,stats.getGarbageCollector());
	    	addAll(payload,stats.getClassLoaderStats());
	    	addAll(payload,stats.getMemoryPoolCollector());
	    	addAll(payload,stats.getMemoryStatCollector());
	    	
	    	sendMetrics(ImmutableMap.copyOf(payload),stats.getTimestamp());
	    	
    	} catch(Exception e) {
        	System.err.println(e.getMessage());
        	e.printStackTrace();
        }
    }
    
    private void addAll(Map<String,Double> collection,ImmutableMap<String, Map<String, Double>> maps) {
    	
    	for(String key : maps.keySet()) {   		
    		for(String metricName : maps.get(key).keySet()) {
    			String prefix = this.graphitePrefix + "." + key + "." + metricName;
    			collection.put(prefix, maps.get(key).get(metricName));
    		}
    	}
    	
    }
    
    
    void destroy() {
     //  statsd.stop();
    }
    
    @Override 
    public void shutdown() {
        queue.poll();
        queue.offer(POISON);
        try {
            System.err.println("Waiting for the thread to die");
            thread.join(10000L);
        } catch (InterruptedException ex) {
            thread.interrupt();
        }
        destroy();
    }
    
    @Override
    public void run() {
    
        if (isRunning.compareAndSet(false, true)) {            
            System.err.println("Starting jvm Message producer");
            
            Runner runner = new Runner(this);
            thread = new Thread(runner);
            thread.start();
            
        } else {
            System.err.println("An existing instance of jvm message producer is already running");
        }
    }
    
    
   
    /**
     * Send a set of metrics with a given timestamp to graphite.
     *  
     * @param metrics the metrics as key-value-pairs
     * @param timeStamp the timestamp
     * @throws GraphiteException 
     */
    public void sendMetrics(final Map<String, ? extends Number> metrics, final long timeStamp) {
            Socket socket = null;
            try {
                    socket = createSocket();
                    OutputStream s = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(s, true);
                    for (String key: metrics.keySet()) {
                            String metricKey = key.replace(' ', '_');
                            String message = String.format("%s %.2f %d%n", 
                                            metricKey, metrics.get(key), timeStamp);
                            //System.err.println(message);
                            out.printf(message);    
                    }    
                    out.close();
                    socket.close();
                    socket = null;
            } catch (UnknownHostException e) {
                    
            } catch (IOException e) {
                     
            } finally {
                    if(socket != null)  {
                            try {
                                    socket.close();
                                    socket = null;
                            } catch (IOException ex) {ex.printStackTrace();}
                    }
            }
    }

    
    protected Socket createSocket() throws UnknownHostException, IOException {
        return new Socket(graphiteHost, Integer.valueOf(graphitePort));
    }
    
    public static class Runner implements Runnable {

        private final JvmMessageProducer jvmMessageProducer;
        
        private AtomicBoolean isBrokerAvailable = new AtomicBoolean(false);
        
        public Runner(JvmMessageProducer jvmMessageProducer) {
            this.jvmMessageProducer = jvmMessageProducer;
        }
        
        @Override
        public void run() {
            Stats msg = null;
           // System.err.println("0");
            try {
                while (!(msg = jvmMessageProducer.getQueue().take()).equals(POISON)) {
                    //System.err.println("1");    
                    if (!isBrokerAvailable.get()) {
                        try {
                           // System.err.println("2");
                            jvmMessageProducer.setup();
                            isBrokerAvailable.set(true);
                        } catch (Exception ex) {
                            isBrokerAvailable.set(false);
                            System.err.println("Error in configuring monitor, please check "
                                    + "if the agent is up and running "
                                    + "on port : "+ jvmMessageProducer.getGraphitePort() );
                        }
                                                
                    } else {                        
                        try {
                            //System.err.println("3");
                            jvmMessageProducer.send(msg);
                           // System.err.println("4");
                        } catch (Exception ex) {
                            ((JvmMessageProducerImpl) jvmMessageProducer).destroy();
                            isBrokerAvailable.set(false);
                            System.err.println("Error in sending message to the agent, please check if" 
                                    + " agent is up and running on port : {}"+ 
                                     jvmMessageProducer.getGraphitePort());
                        }                        
                       // System.err.println("5");
                    }                                       
                }
                System.err.println("POSIONED, shutting down");
            } catch (Throwable t) {
                isBrokerAvailable.set(false);                
            }
        }
        
    }
    
    

}
