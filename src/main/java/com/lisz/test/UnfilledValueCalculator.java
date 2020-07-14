package com.lisz.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("unfilled_values")
public class UnfilledValueCalculator extends Calculator {
	@Value("${metrics.unfilled_values}")
	private String key;

	public Map<KPI, Long> calculate(ValueRecord record) {
		Map<KPI, Long> kpis = new HashMap<>();
		kpis.put(KPI.ASIN, record.getTotalAsins() * 2);
		kpis.put(KPI.GV, record.getTotalGV() * 3);
		return kpis;
	}

	@Override
	protected String getKey() {
		return key;
	}
}
