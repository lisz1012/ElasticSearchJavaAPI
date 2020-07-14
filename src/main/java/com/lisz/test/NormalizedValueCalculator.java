package com.lisz.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("normalized_values")
public class NormalizedValueCalculator extends Calculator {
	@Value("${metrics.normalized_values}")
	private String key;

	@Override
	protected String getKey() {
		return key;
	}
}
