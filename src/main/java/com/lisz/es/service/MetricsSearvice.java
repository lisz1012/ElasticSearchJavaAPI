package com.lisz.es.service;

import com.lisz.test.Calculator;
import com.lisz.test.KPI;
import com.lisz.test.ValueRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetricsSearvice implements InitializingBean {

	private List<Calculator> calculators = new ArrayList<>();

	@Autowired
	@Qualifier("total_values")
	private Calculator totalValueCalculator;
	@Autowired
	@Qualifier("normalized_values")
	private Calculator normalizedValueCalculator;
	@Autowired
	@Qualifier("unfilled_values")
	private Calculator unfilledValueCalculator;

	@Override
	public void afterPropertiesSet() throws Exception {
		calculators.add(totalValueCalculator);
		calculators.add(normalizedValueCalculator);
		calculators.add(unfilledValueCalculator);
	}
	public void test() {
		Map<String, Map<KPI, Long>> res = new HashMap<>();
		ValueRecord record = new ValueRecord(10L, 6L);
		calculators.forEach(c -> {
			c.calculate(res, record);
		});
		System.out.println(res);
	}
}
