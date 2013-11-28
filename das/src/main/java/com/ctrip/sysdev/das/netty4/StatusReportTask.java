package com.ctrip.sysdev.das.netty4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DalServer;

public class StatusReportTask implements Runnable {
	private static final String URL_TEMPLATE = "http://%s/console/dal/das/monitor/timeCosts";

	private String DECODE_TIME_TEMPLATE = "id=%s&timeCost=decodeRequestTime:%d";
	private String DB_TIME_TEMPLATE = "id=%s&timeCost=dbTime:%d";
	private String ENCODE_TIME_TEMPLATE = "id=%s&timeCost=encodeResponseTime:%d";
	
	private static final int DEFAULT_BATCH_COUNT = 100;
	
	private static ScheduledExecutorService sender;
	private static StatusReportTask instance;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Queue<String> statusQueue;
	private int batchCount;
	private URL consoleUrl;
	private boolean connectionLost;

	public static void initInstance(String consoleAddr, int batchCount) throws MalformedURLException {
		instance = new StatusReportTask(consoleAddr, batchCount);
		sender = Executors.newSingleThreadScheduledExecutor();
		sender.scheduleAtFixedRate(instance, 1, 1, TimeUnit.SECONDS);
	}
	
	public static void shutdown() {
		sender.shutdown();
	}
	
	public static StatusReportTask getInstance() {
		return instance;
	}
	
	private StatusReportTask(String consoleAddr, int batchCount)
			throws MalformedURLException {
		this.statusQueue = new ConcurrentLinkedQueue<String>();
		this.batchCount = batchCount < 1 ? DEFAULT_BATCH_COUNT : batchCount;
		consoleUrl = new URL(String.format(URL_TEMPLATE, consoleAddr));
	}

	public void recordDecodeEnd(String id, long start) {
		addStatus(String.format(DECODE_TIME_TEMPLATE, id, System.currentTimeMillis() - start));
	}
	
	public void recordDbEnd(String id, long start) {
		addStatus(String.format(DB_TIME_TEMPLATE, id, System.currentTimeMillis() - start));
	}
	
	public void recordEncodeEnd(String id, long start) {
		addStatus(String.format(ENCODE_TIME_TEMPLATE, id, System.currentTimeMillis() - start));
	}

	public void addStatus(String value) {
		if (!connectionLost)
			statusQueue.offer(value);
	}

	@Override
	public void run() {
		if (statusQueue.isEmpty())
			return;
		
		if(!DalServer.senderEnabled){
			while(statusQueue.poll() != null);
			return;
		}

		URLConnection conn = getURLConnection();
		if (conn == null)
			return;

		reportStatus(conn);

		finishReport(conn);
	}

	private URLConnection getURLConnection() {
		try {
			URLConnection conn = consoleUrl.openConnection();
			conn.setDoOutput(true);
			connectionLost = false;
			return conn;
		} catch (Throwable e) {
			logger.error("Cannot create connection to console", e);
			logger.error("Any new status will be lost", e);
			connectionLost = true;
			return null;
		}
	}

	private void reportStatus(URLConnection conn) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					conn.getOutputStream()));

			int count = 0;
			while (count++ < batchCount) {
				String value = statusQueue.poll();
				if (value == null)
					break;
				writer.write(value);
			}

			writer.flush();
			writer.close();
		} catch (Throwable e) {
			logger.error("Cannot report status to console", e);
			close(writer);
		}
	}

	private void finishReport(URLConnection conn) {
		BufferedReader breader = null;
		try {
			breader = new BufferedReader(new InputStreamReader(conn.getInputStream(),
					"utf-8"));
			while ((breader.readLine()) != null) {
			}
			breader.close();
		} catch (Throwable e) {
			logger.error("Cannot get response from console", e);
			close(breader);
		}
	}
	
	private void close(Closeable closeable) {
		if(closeable == null)
			return;

		try {
			closeable.close();
		} catch (IOException e) {
			logger.error("Cannot close stream", e);
		}
	}
}
