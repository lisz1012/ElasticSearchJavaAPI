package com.lisz.test;

import java.util.HashMap;
import java.util.Map;

public class NormalizedValueCalculator extends Calculator {
	private static final String KEY = "normalized_values";

	@Override
	protected String getKey() {
		return KEY;
	}
}
