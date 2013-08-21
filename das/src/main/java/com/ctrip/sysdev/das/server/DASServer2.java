package com.ctrip.sysdev.das.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.common.AbstractServer;
import com.ctrip.sysdev.das.common.Server;
import com.ctrip.sysdev.das.exception.ServerException;
import com.ctrip.sysdev.das.net.NettyServer;
import com.ctrip.sysdev.das.utils.Consts;

/**
 * 
 * @author weiw
 * 
 */
public class DASServer2 extends AbstractServer {

	private static final Logger logger = LoggerFactory
			.getLogger(DASServer2.class);

	private Server nettyServer;

	@Override
	public void doStartup() throws ServerException {

		nettyServer = new NettyServer(System.getProperty("serverAddr",
				Consts.serverAddr), Integer.parseInt(System.getProperty(
				"serverPort", Consts.serverPort + "")));
		nettyServer.start();
		String time = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]")
				.format(new Date());
		logger.warn("*******************************************************************");
		logger.warn("*******************************************************************");
		logger.warn("*******************************************************************");
		logger.warn("**********" + time + "dal server started!"
				+ "***************");
		logger.warn("*******************************************************************");
		logger.warn("*******************************************************************");
		logger.warn("*******************************************************************");
	}

	@Override
	protected void run() {
		while (isRunning()) {
			try {
				if (nettyServer == null || !nettyServer.isStarted()) {
					logger.warn("dal netty server is shutdown");
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void triggerShutdown() {
		System.out.println("Stop  ...");
		if (nettyServer != null && nettyServer.isStarted())
			nettyServer.stop();
		System.out.println("Stopped.");
	}

	public static void main(String[] args) {
		DASServer2 server = new DASServer2();
		server.startUp();
	}

}
