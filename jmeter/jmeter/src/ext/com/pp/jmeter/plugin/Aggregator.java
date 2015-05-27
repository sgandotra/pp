package com.pp.jmeter.plugin;

import java.math.BigDecimal;

public interface Aggregator {
	
	public interface BigDecimals {
		
		BigDecimal nextBigDecimalValue();
		
		boolean hasNextValue();
	}
	
	BigDecimal runBigDecimal(BigDecimals values);

}
