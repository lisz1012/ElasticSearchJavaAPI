package com.lisz.es.controller;

import com.lisz.test.Calculator;
import com.lisz.test.KPI;
import com.lisz.test.TotalValueCalculator;
import com.lisz.test.ValueRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController implements InitializingBean {

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

	@GetMapping("/metrics")
	public String test() {
		Map<String, Map<KPI, Long>> res = new HashMap<>();
		ValueRecord record = new ValueRecord(10L, 6L);
		calculators.forEach(c -> {
			c.calculate(res, record);
		});
		System.out.println(res);
		return "OK";
	}
}
