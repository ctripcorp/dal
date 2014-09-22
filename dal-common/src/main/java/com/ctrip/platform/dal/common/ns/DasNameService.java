package com.ctrip.platform.dal.common.ns;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ctrip.platform.dal.common.to.DasWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DasNameService implements Runnable {
	private String DB_URL_TEMPLATE = "http://%s/rest/instance/dbNode/";
	private String DB_GROUP_URL_TEMPLATE = "http://%s/rest/instance/dbGroupNode/";
	private String dbUrl;
	private String dbGroupUrl;
	
	private ObjectMapper mapper = new ObjectMapper();
	private ScheduledExecutorService reader;
	
	// TODO add shutdown hook
	public DasNameService(String host) {
		this.dbUrl = String.format(DB_URL_TEMPLATE, host);
		this.dbGroupUrl = String.format(DB_GROUP_URL_TEMPLATE, host);
		reader = Executors.newSingleThreadScheduledExecutor();
		reader.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
	}
	
	public List<DasWorker> getByLogicDbGroup(String logicDbGroup) {
		try {
			return mapper.readValue(new URL(dbGroupUrl + logicDbGroup), new TypeReference<List<DasWorker>>(){});
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public List<DasWorker> getByLogicDb(String logicDb) {
		try {
			return mapper.readValue(new URL(dbUrl + logicDb), new TypeReference<List<DasWorker>>(){});
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public static void main(String[] args) {
		DasNameService ns = new DasNameService(args[0]);
		List<DasWorker> workers = ns.getByLogicDb(args[1]);
		for(DasWorker worker: workers) {
			System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
		}
		
		workers = ns.getByLogicDbGroup(args[2]);
		for(DasWorker worker: workers) {
			System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
		}
	}

	public void run() {
		//TODO Should sync with server
	}
}
