package com.ctrip.sysdev.das.common.cfg;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ctrip.sysdev.das.common.to.DasConfigure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DasConfigureService implements Runnable {
	private String SNAPSHOT_URL_TEMPLATE = "http://%s/rest/configure/snapshot";
	private String snapshotUrl;
	
	private File snapshotFile;
	private DasConfigure snapshot;
	private ObjectMapper mapper = new ObjectMapper();
	private ScheduledExecutorService reader;
	
	// Sync every 5 minutes
	private static int PERIOD = 60 *1000 * 5;
	
	// TODO add shutdown hook
	public DasConfigureService(String host, File snapshot) {
		snapshotUrl = String.format(SNAPSHOT_URL_TEMPLATE, host);
		this.snapshotFile = snapshot;
		reader = Executors.newSingleThreadScheduledExecutor();
		reader.scheduleAtFixedRate(this, 1, PERIOD, TimeUnit.SECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {reader.shutdown();}
		});
	}

	public void syncSnapshot() {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			snapshot = mapper.readValue(new URL(snapshotUrl), DasConfigure.class);
			mapper.writeValue(snapshotFile, snapshot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DasConfigure getSnapshot() {
		return snapshot;
	}
	
	public DasConfigure loadSnapshot() {
		try {
			snapshot = mapper.readValue(snapshotFile, DasConfigure.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return snapshot;
	}
	
	@Override
	public void run() {
		syncSnapshot();
	}
	
	public static void main(String[] args) {
		DasConfigureService ns = new DasConfigureService(args[0], new File("e:/test.json"));
		ns.syncSnapshot();
		ns.loadSnapshot();
	}
}
