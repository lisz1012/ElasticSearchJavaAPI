package com.lisz.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test1 {
	@Test
	public void test() {
		List<Calculator> calculators = new ArrayList<>();
		calculators.add(new TotalValueCalculator());
		calculators.add(new NormalizedValueCalculator());
		calculators.add(new UnfilledValueCalculator());
		Map<String, Map<KPI, Long>> res = new HashMap<>();
		ValueRecord record = new ValueRecord(10L, 6L);
		calculators.forEach(c -> {
			c.calculate(res, record);
		});
		System.out.println(res);
	}
}
