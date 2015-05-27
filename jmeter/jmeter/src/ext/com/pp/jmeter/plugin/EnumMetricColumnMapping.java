package com.pp.jmeter.plugin;


/**
 * Provides the current metrics understood by the JMeter Plugin and the index of metric in
 * the jmeter record.
 * 
 * 
 * @author sagandotra
 *
 */

public enum EnumMetricColumnMapping {

	/* JMETER MAPPING */
	
	LABEL(0),
	THREADS(2),
	SAMPLES(3),
	AVERAGE(4),
	LAST_RESPONSE_TIME(5),
	MIN(6),
	MAX(7),
	STDDEV(8),
	_95PERCENTILE(9),
	ERRORPERCENT(10),
	THROUGHPUT(11),
	LAST_10_TP(12),
	KBYTESEC(13),
	AVGBYTES(14),
	
	/*TIMESTAMP*/
	ZZZZ(2);
	
	EnumMetricColumnMapping(Integer indx) {
		this.indx = indx;
	}
	
	public Integer indx() {
		return indx;
	}

	private final Integer indx;
	
}
