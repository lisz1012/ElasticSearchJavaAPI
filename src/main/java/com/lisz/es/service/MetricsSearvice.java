package com.lisz.es.service;

import com.lisz.test.Calculator;
import com.lisz.test.KPI;
import com.lisz.test.ValueRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConfigurationProperties(prefix = "props")
public class MetricsSearvice {
	@Autowired
	private List<Calculator> calculators;

	public void test() {
		Map<String, Map<KPI, Long>> res = new HashMap<>();
		ValueRecord record = new ValueRecord(10L, 6L);
		calculators.forEach(c -> {
			System.out.println(c.getClass().getSimpleName() + "is calculating ...");
			c.calculate(res, record);
		});
		System.out.println(res);
	}
}
