package com.ctrip.datasource;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;

public class MetricTest {

	private static IMetric metricLogger = MetricManager.getMetricer();
	
	private static final String DataSource_Type = "arch.dal.datasource.type";
	private Map<String, String> jndiTag = new HashMap<String, String>();
	private Map<String, String> localTag = new HashMap<String, String>();
	
	@Before
	public void setUp() throws Exception {
		jndiTag.put("source", "jndi");
		localTag.put("source", "local");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		for (int i=0;i<100;i++) {
			metricLogger.log(DataSource_Type, 1L, jndiTag);
		}
		for (int i=0;i<100;i++) {
			metricLogger.log(DataSource_Type, 1L, localTag);
		}
		
	}

}
