package com.ctrip.sysdev.das.monitors;

import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DalServer;

public class ErrorReporter implements Runnable {
	private static final String EXCEPTION_URL_TEMPLATE = "http://%s/rest/console/monitor/exceptions";

	private static String CHANNEL_EXCEPTION_TEMPLATE = "ip=%s&id=%s&msg=%s&ts=%d";
	private static String EXECUTION_EXCEPTION_TEMPLATE = "reqId=%s&" + CHANNEL_EXCEPTION_TEMPLATE;
	
	private static ExecutorService sender;
	private static String ip;
	private static String workerId;
	private static URL consoleUrl;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String payload;

	public static void initInstance(String ip, String workerId, String consoleAddr) throws MalformedURLException {
		ErrorReporter.ip = ip;
		ErrorReporter.workerId = workerId;
		ErrorReporter.consoleUrl = new URL(String.format(EXCEPTION_URL_TEMPLATE, consoleAddr));
		sender = Executors.newSingleThreadExecutor();
	}
	
	public static void shutdown() {
		sender.shutdown();
	}
	
	public static void reportChannelException(ChannelHandlerContext ctx, Throwable cause) {
		addException(String.format(CHANNEL_EXCEPTION_TEMPLATE, ip, workerId, cause.getMessage(), System.currentTimeMillis()));
	}
	
	// TODO add more detail message
	public static void reportException(String id, Throwable cause) {
		addException(String.format(EXECUTION_EXCEPTION_TEMPLATE, ip, workerId, id, cause.getMessage(), System.currentTimeMillis()));
	}
	
	private static void addException(String value) {
		sender.execute(new ErrorReporter(value));
	}

	private ErrorReporter(String payload) {
		this.payload = payload;
	}
	
	@Override
	public void run() {
		if(!DalServer.senderEnabled)
			return;

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
			return conn;
		} catch (Throwable e) {
			logger.error("Cannot create connection to console", e);
			logger.error("Any new exception will be lost", e);
			return null;
		}
	}

	private void reportStatus(URLConnection conn) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					conn.getOutputStream()));
			writer.write(payload);
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
