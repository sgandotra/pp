package com.pp.jmeter.plugin.model;

import java.math.BigDecimal;
import java.util.List;

import com.pp.jmeter.plugin.Aggregator;

public class DataPoints implements Aggregator.BigDecimals {

	private List<BigDecimal> dataPoints;
	
	public DataPoints(List<BigDecimal> dataPoints) {
		this.dataPoints = dataPoints;
	}
	
	@Override
	public BigDecimal nextBigDecimalValue() {
		return this.dataPoints.remove(0);
	}

	@Override
	public boolean hasNextValue() {
		return this.dataPoints.size() > 0;
	}

}
