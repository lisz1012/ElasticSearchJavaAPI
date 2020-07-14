package com.lisz.test;

import java.util.HashMap;
import java.util.Map;

public class UnfilledValueCalculator extends Calculator {
	private static final String KEY = "unfilled_values";

	public Map<KPI, Long> calculate(ValueRecord record) {
		Map<KPI, Long> kpis = new HashMap<>();
		kpis.put(KPI.ASIN, record.getTotalAsins() * 2);
		kpis.put(KPI.GV, record.getTotalGV() * 3);
		return kpis;
	}

	@Override
	protected String getKey() {
		return KEY;
	}
}
