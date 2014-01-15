package com.ctrip.sysdev.das;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.common.db.DruidDataSourceWrapper;
import com.ctrip.sysdev.das.monitors.ErrorReporter;
import com.ctrip.sysdev.das.monitors.PerformanceMonitorTask;
import com.ctrip.sysdev.das.monitors.StatusReportTask;
import com.ctrip.sysdev.das.netty4.Netty4Server;

/**
 * @deprecated this is only for stress test
 * @author jhhe
 *
 */
public class ZklessDas {
	private static Logger logger = LoggerFactory.getLogger(DalServer.class);
	public static boolean senderEnabled = true;
	public static String consoleAddr = "localhost:8080";
	public static double GC_THRESHHOLD = 0.70;

	private String port;
	private String ip;

	private Netty4Server dasService;
	public ZklessDas(String port) {
		this.port = port;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}
	}

	protected boolean register() {
		try {
			GuiceObjectFactory factory = new GuiceObjectFactory();
//			DruidDataSourceWrapper ds = factory.getInstance(DruidDataSourceWrapper.class);
//			ds.initDataSourceWrapper();
			
			StatusReportTask.initInstance(consoleAddr, 50);
			ErrorReporter.initInstance(ip, port, consoleAddr);
			
			dasService = factory.getInstance(Netty4Server.class);
			dasService.start(Integer.parseInt(port));
			
			PerformanceMonitorTask.start(port, ip);
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					shutdown();
				}
			}));

			return true;
		} catch (Throwable e) {
			logger.error("Error during register worker path", e);
			return false;
		}
	}

	protected void shutdown() {
		try{
			dasService.stop();
		} catch (Throwable e) {
			logger.error("Error during shutdown worker", e);
		}
		
		PerformanceMonitorTask.shutdown();
		StatusReportTask.shutdown();
		ErrorReporter.shutdown();
	}

	public static void main(String[] args) {
		logger.info("Started at port " + args[0]);
		try {
			if(args.length > 2) {
				DalServer.consoleAddr = args[2];
			}
			
			if(args.length > 3) {
				DalServer.senderEnabled = Boolean.parseBoolean(args[3]);
			}

			new ZklessDas(args[0]).register();
		} catch (Exception e) {
			logger.error("Error starting server", e);
		}
		logger.info("DAS started");
	}
}