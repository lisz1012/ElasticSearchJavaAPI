package com.lisz.es.controller;

import com.lisz.es.service.MetricsSearvice;
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
public class TestController {

	@Autowired
	private MetricsSearvice service;

	@GetMapping("/metrics")
	public String test() {
		service.test();
		return "OK";
	}
}
