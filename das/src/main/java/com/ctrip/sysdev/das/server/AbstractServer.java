package com.ctrip.sysdev.das.server;

import com.ctrip.sysdev.das.exception.ServerException;
import com.ctrip.sysdev.das.jmx.MBeanUtil;
import com.ctrip.sysdev.das.jmx.ServerInfoMXBean;
import com.ctrip.sysdev.das.utils.SingleInstanceDaemonTool;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;

/**
 * 
 * @author weiw
 * 
 */
public abstract class AbstractServer extends AbstractExecutionThreadService {

	public abstract void doStartup() throws ServerException;

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

}
