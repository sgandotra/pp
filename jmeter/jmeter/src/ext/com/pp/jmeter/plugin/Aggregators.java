package com.pp.jmeter.plugin;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.pp.jmeter.plugin.Aggregator;

public class Aggregators {

	private static final Aggregator average = new Average("average");
	private static final Map<String,Aggregator> aggregators = new HashMap<String,Aggregator>();
	
	static {
		aggregators.put("average", average);
	}
	
	private Aggregators() {} 
	
	public static Aggregator get(String name) {
		Aggregator aggregator = aggregators.get(name);
		if(aggregator != null)
			return aggregator;
		else
			throw new NoSuchElementException("Element not found");
	}
	
	
	public static final class Average implements Aggregator {
		private final String name;
		
		public Average(String name) {
			this.name = name;
		}

		@Override
		public BigDecimal runBigDecimal(BigDecimals values) {
			
			BigDecimal result = values.nextBigDecimalValue();
			int n = 1;
			while(values.hasNextValue()) {
				result = result.add(values.nextBigDecimalValue());
				n++;
			}
			
			return result.divide(new BigDecimal(n),new MathContext(5));
			
		}
		
	}
}
