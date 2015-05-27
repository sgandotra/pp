package com.pp.jmeter.plugin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class GraphitePublisher {

    private static final Logger logger = LoggingManager.getLoggerForClass();

	
	private final String graphiteHost;
	private final int graphitePort;
	private final String folderName;
	
	private AgentConfigurer agentConfigurer;
	
	/**
	 * Create a new Graphite client.
	 * 
	 * @param graphiteHost Host name to write to.
	 * @param graphitePort Graphite socket. Default is 2003
	 */
	public GraphitePublisher(String graphiteHost, int graphitePort,String folderName) {
		this.graphiteHost = graphiteHost;
		this.graphitePort = graphitePort;
		this.folderName   = folderName;
	}
	
	public GraphitePublisher() {
		
		agentConfigurer = AgentConfigurer.getInstance();
		
		//initialize from configurer, default values
		this.graphiteHost = agentConfigurer.getHostName();
		this.graphitePort = agentConfigurer.getPort();
		this.folderName   = agentConfigurer.getFolderName();
		
	}

	/**
	 * Send a set of metrics with the current time as timestamp to graphite.
	 *  
	 * @param metrics the metrics as key-value-pairs
	 * @throws GraphiteException 
	 */
	public void sendMetrics(Map<String, ? extends Number> metrics) throws GraphiteException {
		sendMetrics(metrics, getCurrentTimestamp());
	}

	/**
	 * Send a set of metrics with a given timestamp to graphite.
	 *  
	 * @param metrics the metrics as key-value-pairs
	 * @param timeStamp the timestamp
	 * @throws GraphiteException 
	 */
	public void sendMetrics(Map<String, ? extends Number> metrics, long timeStamp) throws GraphiteException {
		Socket socket = null;
		try {
			socket = createSocket();
			OutputStream s = socket.getOutputStream();
			PrintWriter out = new PrintWriter(s, true);
			for (Map.Entry<String, ? extends Number> metric: metrics.entrySet()) {
				String metricKey = metric.getKey().replace(' ', '_');
				String message = String.format("%s %.2f %d%n", 
						this.folderName + "." + metricKey, metric.getValue(), timeStamp);
				//logger.info(message);
				out.printf(message);	
			}			
			out.close();
			socket.close();
			socket = null;
		} catch (UnknownHostException e) {
			throw new GraphiteException("Unknown host: " + graphiteHost);
		} catch (IOException e) {
			throw new GraphiteException("Error while writing data to graphite: " + e.getMessage(), e);
		} finally {
			if(socket != null)  {
				try {
					socket.close();
					socket = null;
				} catch (IOException ex) {ex.printStackTrace();}
			}
		}
	}
	
	
	/**
	 * Send a single metric with the current time as timestamp to graphite. 
	 * 
	 * @param key The metric key
	 * @param value the metric value
	 * 
	 * @throws GraphiteException if writing to graphite fails
	 */
	public void sendMetric(String key, int value) throws GraphiteException {
		sendMetric(key, value, getCurrentTimestamp());
	}
	
	/**
	 * Send a single metric with a given timestamp to graphite.
	 * 
	 * @param key The metric key
	 * @param value The metric value
	 * @param timeStamp the timestamp to use
	 * 
	 * @throws GraphiteException if writing to graphite fails 
	 */
	@SuppressWarnings("serial")
	public void sendMetric(final String key, final int value, long timeStamp) throws GraphiteException {		
		sendMetrics(new HashMap<String, Integer>() {{
			put(key, value);
		}}, timeStamp);
	}
	
	protected Socket createSocket() throws UnknownHostException, IOException {
		logger.info("Sending request to " + graphiteHost + " port " + graphitePort);
		return new Socket(graphiteHost, graphitePort);
	}
	 
	/***
	 * Compute the current graphite timestamp.
	 * 
	 * @return Seconds passed since 1.1.1970
	 */
	protected long getCurrentTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
}
