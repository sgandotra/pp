package com.pp.jmeter.plugin;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.Sample;
import org.apache.jmeter.visualizers.SamplingStatCalculator;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;


public class SamplingStatCalculatorExt extends SamplingStatCalculator {
    private static final Logger logger = LoggingManager.getLoggerForClass();

	private volatile double sampleCount  = 0.0;
	private double averageThroughput 	    = 0.0;
	private long startingTimestamp			= System.currentTimeMillis();
			
	public SamplingStatCalculatorExt(String sampleLabel) {
		super(sampleLabel);
		
	}
	
	public Sample addSample(SampleResult res) {
		Sample s = super.addSample(res);
		sampleCount++;	
		
		return s;
		
	}

	public double getLastMeanAsNumber() {
		
		double elapsed =  ( this.getCurrentSample().getEndTime() -
				this.getCurrentSample().getStartTime());
		return elapsed;
	}
	
	public double getLastTenRate() {
		logger.info("Sample count : " + this.sampleCount);
		synchronized(this) {
			if(sampleCount >= 10) {
				long now		  = System.currentTimeMillis();
				averageThroughput = (1000 * sampleCount / (now - startingTimestamp));
				sampleCount  		= 0;
				startingTimestamp = System.currentTimeMillis();
			}
		}

		return averageThroughput;
	}
		
}
