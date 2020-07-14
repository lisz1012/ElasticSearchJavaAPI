package com.lisz.test;

import java.util.HashMap;
import java.util.Map;

public abstract class Calculator {
	public Map<KPI, Long> calculate(ValueRecord record) {
		Map<KPI, Long> kpis = new HashMap<>();
		kpis.put(KPI.ASIN, record.getTotalAsins());
		kpis.put(KPI.GV, record.getTotalGV());
		return kpis;
	}

	public void calculate(Map<String, Map<KPI, Long>> map, ValueRecord record) {
		Map<KPI, Long> kpis = calculate(record);
		map.put(getKey(), kpis);
	}

	protected abstract String getKey();
}
