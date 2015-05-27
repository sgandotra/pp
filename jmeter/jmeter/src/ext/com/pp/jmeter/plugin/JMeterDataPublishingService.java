package com.pp.jmeter.plugin;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.pp.jmeter.plugin.model.DataPoints;



/**
 * Run by ScheduledThreadExecutor, every X seconds, and aggregates all the data
 * that has been inserted into our shared list. 
 * 
 * 
 * @author sagandotra
 *
 */

public class JMeterDataPublishingService implements Runnable {

    private static final Logger logger = LoggingManager.getLoggerForClass();

	private static final String ISO_FORMAT_NO_MILLIS 		 = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat dateFormat		 = new SimpleDateFormat(ISO_FORMAT_NO_MILLIS);

    
    private final List<String> collectedData;
    	
	private Set<String> zzzz;	
	private Map<String,EnumMap> steps;
	
	/**
	 * 
	 * Constructor for initializing the data collector and configurer, marked as final immutable
	 * 
	 * @param collectedData
	 */
	
	public JMeterDataPublishingService(final List<String> collectedData) {
		this.collectedData = collectedData;
	}

	
	/**
	 * The job of the thread is to simply aggregate all the data and average it out using BigDecimal datatype
	 * 
	 * 
	 */

	@Override
	public void run() {
		logger.info("Starting data publication");
		
		this.zzzz          = new HashSet<String>();
		this.steps		   = new HashMap<String,EnumMap>();
		
		try {
			logger.info("Size of queue : " +collectedData.size());
			while(collectedData.size()  > 0) {	
				String jmeterRecord	 	= collectedData.remove(0);
				String[] jmeterTokens 	= jmeterRecord.split(","); 
				String label			= jmeterTokens[EnumMetricColumnMapping.LABEL.indx()];
				
				if(label.equals("ZZZZ")) {
					this.zzzz.add(jmeterTokens[EnumMetricColumnMapping.ZZZZ.indx()]);
				} else {
					processLabels(jmeterTokens);
				}	
				
			}
			
			if(steps.size() > 0)
				aggregate();
			
		}
		catch(Throwable t) {
			logger.error("ERROR "+t.getLocalizedMessage());
			logger.error("ERROR :"+t.toString());
			for(StackTraceElement ste : t.getStackTrace()) 
				logger.error("ERROR :"+ste.getLineNumber() + " : " + ste.getClassName() + " " +ste.getMethodName());
		}
	}
	
	/**
	 * This is a global container for storing all Jmeter label associated with the different 
	 * datapoints {@link EnumMetricColumnMapping} 
	 * 
	 * 
	 * @param jmeterTokens
	 */
	protected void processLabels(String[] jmeterTokens) {
		//get label
		String label = jmeterTokens[EnumMetricColumnMapping.LABEL.indx()];
		
		//check if it exists
		if(!steps.containsKey(label)) {
			//if does not exist create a new enum map
			steps.put(label, new EnumMap<EnumMetricColumnMapping, List<BigDecimal>>(EnumMetricColumnMapping.class));
		}
		//process the individual records in jmeter tokens and add to that enumMap
		processLabel(label,jmeterTokens);
		
	}
	
	/**
	 * Process each label and the tokens associated with it.
	 * 
	 * 
	 * @param label
	 * @param jmeterTokens
	 */
	
	protected void processLabel(String label,String[] jmeterTokens) {
	
		EnumMetricColumnMapping[] enums = EnumMetricColumnMapping.values();
		EnumMap<EnumMetricColumnMapping, List<BigDecimal>> enumMap = steps.get(label);
		
		for(EnumMetricColumnMapping _enum : enums) {
			if(!_enum.name().equals("ZZZZ") && !_enum.name().equals("LABEL")) {
				
				if(!enumMap.containsKey(_enum)) 
					enumMap.put(_enum, new ArrayList<BigDecimal>());
				
				if(jmeterTokens[_enum.indx()] != null) {
					BigDecimal val = new BigDecimal(jmeterTokens[_enum.indx()]);		
					enumMap.get(_enum).add(val);
				}
			}
			
			
		}
	
	}
	
	protected void aggregate()  {
		
		if(steps.size() == 0)
			throw new NoSuchElementException("no more data points to process");
		
		for(String labelName : steps.keySet()) {
			EnumMap<EnumMetricColumnMapping, List<BigDecimal>> enumMap = steps.get(labelName);
			Map<String,Double> resultMap		= new HashMap<String,Double>();
			for(EnumMetricColumnMapping _enum : enumMap.keySet()) {
				List<BigDecimal> data = enumMap.get(_enum);
				DataPoints dataPoints = new DataPoints(data);
				
				Aggregator aggregator = Aggregators.get("average");
				BigDecimal result = aggregator.runBigDecimal(dataPoints);
				
				resultMap.put(labelName + "." + _enum.name().toLowerCase(), result.doubleValue());
			}
			//logger.info(" > " +resultMap.toString());
			publish(resultMap);
			
		}
		
	}
	
	protected void publish(Map<String,Double> resultMap) {
		GraphitePublisher publisher = new GraphitePublisher();
		Long timestamp			    = parseNmonZZZAsDate(getMaxDate()).getTime();
		try {
			publisher.sendMetrics(resultMap,timestamp/1000);
		} catch (GraphiteException ex) {
			logger.debug("Error in publishing to graphite : "+ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	protected String getMaxDate() {
		
		TreeSet<String> sortedSet = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				long _arg0 = parseNmonZZZAsDate(arg0).getTime();
				long _arg1 = parseNmonZZZAsDate(arg1).getTime();
				
				return (int)(_arg0 - _arg1);
			}
			
		});
		sortedSet.addAll(zzzz);
		
		return sortedSet.last();
	}
	
	
	protected static Date parseNmonZZZAsDate(String source)  {
		try {
			return dateFormat.parse(source);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
