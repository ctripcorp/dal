package com.ctrip.sysdev.das.common.ns;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.ctrip.sysdev.das.common.to.DasWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DalNameService {
	private String DB_URL_TEMPLATE = "http://%s/rest/instance/dbNode/";
	private String DB_GROUP_URL_TEMPLATE = "http://%s/rest/instance/dbGroupNode/";
	private String dbUrl;
	private String dbGroupUrl;
	private ObjectMapper mapper = new ObjectMapper();
	
	public DalNameService(String host) {
		this.dbUrl = String.format(DB_URL_TEMPLATE, host);
		this.dbGroupUrl = String.format(DB_GROUP_URL_TEMPLATE, host);
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
	
	public void createSnapshot() {
//		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		//mapper.writeValue(new File("result.json"), myResultObject);
	}
	
	public void loadFromSnapshot() {
//		MyValue value = mapper.readValue(new File("data.json"), MyValue.class);
	}
	public static void main(String[] args) {
		DalNameService ns = new DalNameService(args[0]);
		List<DasWorker> workers = ns.getByLogicDb(args[1]);
		for(DasWorker worker: workers) {
			System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
		}
		
		workers = ns.getByLogicDbGroup(args[2]);
		for(DasWorker worker: workers) {
			System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
		}

	}
}
