package com.lisz.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("total_values")
public class TotalValueCalculator extends Calculator {
	@Value("${metrics.total_values}")
	private String key;

	@Override
	protected String getKey() {
		return key;
	}
}
