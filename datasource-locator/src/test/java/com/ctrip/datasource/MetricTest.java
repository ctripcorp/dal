package com.ctrip.datasource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;

public class MetricTest {

	private static IMetric metricLogger = MetricManager.getMetricer();
	
	private static final String DataSource_Type = "arch.dal.datasource.type";
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		for (int i=0;i<100;i++) {
			metricLogger.log(DataSource_Type, 10L);
		}
		for (int i=0;i<100;i++) {
			metricLogger.log(DataSource_Type, 20L);
		}
		
	}

}
