package com.ctrip.sysdev.das;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceMonitorTask implements Runnable {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("restriction")
	private static com.sun.management.OperatingSystemMXBean osMBean;
	private static String workerId;
	private static String ip;
	private static long lastSystemTime;
	private static long lastProcessorCpuTime;
	
	private static ScheduledExecutorService sender;
	public PerformanceMonitorTask(String workerId) {
		this.workerId = workerId;
	}

	@SuppressWarnings("restriction")
	public static void start(String workerId, String ip) {
		PerformanceMonitorTask.workerId = workerId;
		PerformanceMonitorTask.ip = ip;
		sender = Executors.newSingleThreadScheduledExecutor();
		osMBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		sender.scheduleAtFixedRate(new PerformanceMonitorTask("aaa"), 1, 1, TimeUnit.SECONDS);
		lastSystemTime = System.nanoTime();
		lastProcessorCpuTime = osMBean.getProcessCpuTime();
	}
	
	public static void shutdown() {
		sender.shutdown();
	}

	@SuppressWarnings("restriction")
	@Override
	public void run() {
		URL url;
		String result = "";
		
		
		double systemCpuUsage = osMBean.getSystemCpuLoad();
		double processCpuUsage = getProcessorCpuUsage();
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		
		logger.debug("processCpuUsage/systemCpuUsage/totalMemory/freeMemory" + processCpuUsage + systemCpuUsage + totalMemory + freeMemory);
		
		try {
			url = new URL("http://localhost:8080/console/dal/das/monitor/performance");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());

			StringBuilder sb = new StringBuilder();
			sb.append("id=").append(workerId).
				append("&ip=").append(ip).
				append("&processCpuUsage=").append(processCpuUsage).
				append("&systemCpuUsage=").append(systemCpuUsage).
				append("&freeMemory=").append(freeMemory).
				append("&totalMemory=").append(totalMemory);
			
			result = sb.toString();
			writer.write(sb.toString());
			writer.flush();
			writer.close();
			
			InputStreamReader reder = new InputStreamReader(conn.getInputStream(), "utf-8");  
	        BufferedReader breader = new BufferedReader(reder);  
	        while ((breader.readLine()) != null) {}
	        breader.close();
		} catch (Exception e) {
			logger.error("Error sending performance statistics: " + result, e);
		}
	}
	
	@SuppressWarnings("restriction")
	private double getProcessorCpuUsage() {
		// The following method not work
//		double processcpuUsage = osMBean.getProcessCpuLoad();
		long systemTime = System.nanoTime();
		long processorCpuTime = osMBean.getProcessCpuTime();
		double processorCpuUsage = (processorCpuTime - lastProcessorCpuTime)/(systemTime - lastSystemTime);

		lastSystemTime = systemTime;
		lastProcessorCpuTime = processorCpuTime;
		
		return processorCpuUsage;
	}
}
