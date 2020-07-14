package com.lisz.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
责任链 + 模版方法 设计模式，好处是再添加新的xxx_values的时候这个类几乎不用修改，
只要多写一句calculators.add(new XXXCalculator())就好了
 */
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
