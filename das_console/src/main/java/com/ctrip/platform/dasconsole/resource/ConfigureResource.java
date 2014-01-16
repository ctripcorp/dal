package com.ctrip.platform.dasconsole.resource;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.ctrip.sysdev.das.common.to.DasConfigure;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource
@Path("configure/snapshot")
@Singleton
public class ConfigureResource extends DalBaseResource implements Runnable {
	private ScheduledExecutorService reader;
	// Sync every 5 minutes
	private static int PERIOD = 60 *1000 * 5;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private String latestConfig;
	
	public ConfigureResource() {
		super();
		initZk();
		buildLatest();
	}
	
	@GET
	public String get() {
		if(latestConfig == null)
			buildLatest();
		return latestConfig;
	}
	
	private void buildLatest() {
		DasConfigure config = new DasConfigure();
		
		try {
			config.setPort(getFactory().getPortAccessor().list());
			config.setNode(getFactory().getDasNodeAccessor().list());
			config.setDb(getFactory().getLogicDbAccessor().list());
			config.setDbGroup(getFactory().getLogicDbGroupAccessor().list());
			config.setDeployment(getFactory().getDeploymentAccessor().list());
			latestConfig = mapper.writeValueAsString(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initZk() {
//		getZk().
	}
	
	private void initTimer() {
		reader = Executors.newSingleThreadScheduledExecutor();
		reader.scheduleAtFixedRate(this, PERIOD, PERIOD, TimeUnit.SECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {reader.shutdown();}
		});
	}
	
	private void configureChanged() {
		// TODO shall we refresh by different entity?
		buildLatest();
	}
	
	@Override
	public void run() {
		buildLatest();
	}
}
