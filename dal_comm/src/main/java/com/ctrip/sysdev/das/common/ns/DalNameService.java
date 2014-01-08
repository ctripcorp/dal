package com.ctrip.sysdev.das.common.ns;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.ctrip.sysdev.das.common.to.DasWorker;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DalNameService {
	private String DB_URL_TEMPLATE = "http://%s/rest/instance/dbNode/";
	private String DB_GROUP_URL_TEMPLATE = "http://%s/rest/instance/dbGroupNode/";
	private String dbUrl;
	private String dbGroupUrl;
	
	private File snapshot;
	private ObjectMapper mapper = new ObjectMapper();
	
	public DalNameService(String host, File snapshot) {
		this.dbUrl = String.format(DB_URL_TEMPLATE, host);
		this.dbGroupUrl = String.format(DB_GROUP_URL_TEMPLATE, host);
		this.snapshot = snapshot;
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
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			mapper.writeValue(snapshot, getByLogicDb("dao_test"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadSnapshot() {
		try {
			List<DasWorker> workers = mapper.readValue(snapshot, new TypeReference<List<DasWorker>>(){});
			for(DasWorker worker: workers) {
				System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DalNameService ns = new DalNameService(args[0], new File("e:/test.json"));
		List<DasWorker> workers = ns.getByLogicDb(args[1]);
		for(DasWorker worker: workers) {
			System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
		}
		
		ns.createSnapshot();
		ns.loadSnapshot();
		workers = ns.getByLogicDbGroup(args[2]);
		for(DasWorker worker: workers) {
			System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
		}

	}
}
