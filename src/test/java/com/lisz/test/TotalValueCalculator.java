package com.lisz.test;

import java.util.HashMap;
import java.util.Map;

public class TotalValueCalculator extends Calculator {
	private static final String KEY = "total_values";

	@Override
	protected String getKey() {
		return KEY;
	}
}
