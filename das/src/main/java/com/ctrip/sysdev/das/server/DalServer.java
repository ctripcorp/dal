package com.ctrip.sysdev.das.server;

import com.ctrip.sysdev.das.commons.TcpServer;
import com.ctrip.sysdev.das.exception.ServerException;
import com.google.inject.Inject;

public class DalServer extends AbstractServer {

	private TcpServer tcpServer;

	@Inject
	public DalServer(TcpServer tcpServer) {
		this.tcpServer = tcpServer;
	}

	@Override
	public void doStartup() throws ServerException {
		try {
			tcpServer.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServerException("start dal tcpserver error", e);
		}
	}

	@Override
	protected void run() throws Exception {
		while (isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void triggerShutdown() {
		try {
			tcpServer.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("dal tcpserver Stopped.");
	}

}
