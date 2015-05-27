package com.pp.jmeter.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTable;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.pp.jmeter.plugin.util.CalendarUtils;

/**
 * Central class for adding all the published JMeter data to the Collection in the 
 * accepted format.
 * 
 * 
 * @author sagandotra
 *
 */


public class JMeterDataCollectorService implements Runnable {
	
    private static final Logger log 						 = LoggingManager.getLoggerForClass();

    private static final String METRIC_TIMESTAMP_PREFIX		 = "ZZZZ";
    private static final String METRIC_RECORD_COUNTER_PREFIX = "T";
    
    private static final AtomicInteger counter 		 		 = new AtomicInteger();
    private static final AtomicInteger timeCounter	 		 = new AtomicInteger();
    
    private final List<String> collectedData;
    private final JTable myJTable;
    
    private List<String> data;
    
    /**
     * 
     * Constructor which initializes the service with the configurer, JTable and 
     * datastructure where to store. JTable and AgentConfigurer are final and 
     * thus immutable.
     * 
     * 
     * @param collectedData
     * @param myJTable
     */
	public JMeterDataCollectorService(final List<String> collectedData, final JTable myJTable) {
		
		if(null == collectedData || null == myJTable) 
			throw new IllegalArgumentException("Internal Exception in collector service, ERR-100");
		
		this.collectedData 				 = collectedData;
		this.myJTable	   				 = myJTable;

	}

	/**
	 * When this thread runs it reads the data from JTable and inserts into {@link collectedData}.
	 * 
	 */
	
	@Override
	public void run() {
		try {
			String out = null;
			
			for(int rowIndx = 0 ; rowIndx < myJTable.getModel().getRowCount(); rowIndx++) {
				
				data		   = new ArrayList<String>();
				
				boolean complete = true;
				
				for(int colIndx = 0 ; colIndx < myJTable.getModel().getColumnCount(); colIndx++) {
					String cellValue = myJTable.getModel().getValueAt(rowIndx,colIndx).toString();
				
					data.add(cellValue);
				}
				if(complete) {
					data.add(1,getPrefixedJmeterCounter());
					data.add(2,String.valueOf(JMeterContextService.getNumberOfThreads()));
					out = StringUtils.join(data,",");
					collectedData.add(out);
				}
			}
	
			String timestamp = addFormattedTimestamp();
			collectedData.add(timestamp);
			//log.info("Record written to queue : " +out);
		}
		catch(Throwable t) {
			log.error("ERROR "+t.getLocalizedMessage());
			log.error("ERROR :"+t.toString());
			for(StackTraceElement ste : t.getStackTrace()) 
				log.error("ERROR :"+ste.getLineNumber() + " : " + ste.getClassName() + " " +ste.getMethodName());
		}

	}
	
	/**
	 * Create a formatted timestamp to be added to output, it's of NMON format
	 * 
	 * for eg : ZZZZ,T1,yyyy-mm-dd'T'HH:mi:ss
	 * 
	 * @return
	 */
	
	private String addFormattedTimestamp() {
		data		   = new ArrayList<String>();
		data.add(METRIC_TIMESTAMP_PREFIX);
		data.add(getPrefixedTimeCounter());
		data.add(CalendarUtils.getLocalDateTimeAsISO8601());
		return StringUtils.join(data,",");
		
	}
	
	
	/**
	 * return String formatted to be used a Timestamp counter, T[X], which X 
	 * is an atomic Increment per request
	 * @return
	 */
	private String getPrefixedTimeCounter() {
		return METRIC_RECORD_COUNTER_PREFIX + timeCounter.getAndIncrement();
	}
	
	
	/**
	 * return String formatted to be used a JMeter counter, T[X], which X 
	 * is an atomic Increment per request
	 * @return
	 */
	private String getPrefixedJmeterCounter() {
		return  METRIC_RECORD_COUNTER_PREFIX + counter.getAndIncrement();
	}
	
}
