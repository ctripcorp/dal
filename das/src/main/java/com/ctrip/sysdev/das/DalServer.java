package com.ctrip.sysdev.das;

import com.ctrip.sysdev.das.exception.ServerException;
import com.ctrip.sysdev.das.jmx.MBeanUtil;
import com.ctrip.sysdev.das.jmx.ServerInfoMXBean;
import com.ctrip.sysdev.das.netty4.Netty4Server;
import com.ctrip.sysdev.das.utils.SingleInstanceDaemonTool;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;

public class DalServer extends AbstractExecutionThreadService  {

	private Netty4Server tcpServer;

	@Inject
	private ServerInfoMXBean serverInfoMXBean;

	@Override
	protected void startUp() {
		try {
			doStartup();
			SingleInstanceDaemonTool watcher = SingleInstanceDaemonTool
					.createInstance(serverInfoMXBean.getName());
			watcher.init();
			MBeanUtil.registerMBean(serverInfoMXBean.getName(),
					serverInfoMXBean.getName(), serverInfoMXBean);
		} catch (Throwable e) {
			System.out.print("Throwable,server will shutdown!");
			e.printStackTrace();
			SingleInstanceDaemonTool.bailout(-1);
		}
	}

	@Inject
	public DalServer(Netty4Server tcpServer) {
		this.tcpServer = tcpServer;
	}

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GuiceObjectFactory aGuiceObjectFactory = new GuiceObjectFactory();

		try {
			final Service server = aGuiceObjectFactory.getInstance(DalServer.class);
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					server.stopAndWait();
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
