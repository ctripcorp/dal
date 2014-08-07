package com.ctrip.platform.dal.common.cfg;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.common.to.DasConfigure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DasConfigureService implements Runnable {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String SNAPSHOT_URL_TEMPLATE = "http://%s/rest/configure/snapshot";
	private String snapshotUrl;
	
	private File snapshotFile;
	private AtomicReference<DasConfigure> snapshot = new AtomicReference<DasConfigure>();
	
	private ObjectMapper mapper = new ObjectMapper();
	private ScheduledExecutorService reader;
	
	// Sync every 5 minutes
	private static int PERIOD = 60 *1000 * 5;
	
	public DasConfigureService(String host, File snapshot) {
		snapshotUrl = String.format(SNAPSHOT_URL_TEMPLATE, host);
		this.snapshotFile = snapshot;
		loadSnapshot();
		syncSnapshot();
		startSyncThread();
	}
	
	private void startSyncThread() {
		reader = Executors.newSingleThreadScheduledExecutor();
		reader.scheduleAtFixedRate(this, PERIOD, PERIOD, TimeUnit.SECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {reader.shutdown();}
		});
	}

	/**
	 * Client should call this when configure change detected
	 */
	public void syncSnapshot() {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			snapshot.set(mapper.readValue(new URL(snapshotUrl), DasConfigure.class));
			mapper.writeValue(snapshotFile, getSnapshot());
		} catch (Exception e) {
			logger.error("Unable to sychronize with configure service:" + snapshotUrl, e);
		}
	}
	
	public DasConfigure getSnapshot() {
		return snapshot.get();
	}
	
	public void loadSnapshot() {
		try {
			snapshot.set(mapper.readValue(snapshotFile, DasConfigure.class));
		} catch (Exception e) {
//			logger.error("Unable to load configure snapshot from :" + snapshotFile.getName(), e);
			logger.info("Unable to load configure snapshot from :" + snapshotFile.getName());
		}
	}
	
	@Override
	public void run() {
		syncSnapshot();
	}
}
