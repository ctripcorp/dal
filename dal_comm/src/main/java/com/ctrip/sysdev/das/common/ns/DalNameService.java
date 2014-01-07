package com.ctrip.sysdev.das.common.ns;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.ctrip.sysdev.das.common.to.DasWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DalNameService {
	private String URL_TEMPLATE = "http://%s/rest/instance/dbNode/";
	private String url;
	private ObjectMapper mapper = new ObjectMapper();
	
	public DalNameService(String host) {
		this.url = String.format(URL_TEMPLATE, host);
	}
	
	public List<DasWorker> getDasWorkers(String logicDb) {
		try {
			return mapper.readValue(new URL(url + logicDb), new TypeReference<List<DasWorker>>(){});
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
		List<DasWorker> workers = ns.getDasWorkers(args[1]);
		for(DasWorker worker: workers) {
			System.out.println(String.format("Id: %s  Ports: %d", worker.getId(), worker.getPort()));
		}
	}
}
