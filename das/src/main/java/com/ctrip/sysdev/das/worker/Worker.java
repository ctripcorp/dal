package com.ctrip.sysdev.das.worker;

import java.util.Queue;

import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.ctrip.sysdev.das.domain.RequestMessage;

/**
 * @deprecated
 * @author jhhe
 *
 */
public class Worker extends Thread {
	private String name;
	private volatile boolean stop;
	private DataSourceWrapper dataSource;
	private Queue<RequestMessage> reqQueue;
	private QueryExecutor executor;
	
	public Worker(
			String name, 
			DataSourceWrapper dataSource, 
			Queue<RequestMessage> reqQueue) {
		super(name);
		this.dataSource = dataSource;
		this.reqQueue = reqQueue;
	}
	
	public void setStopFlag() {
		stop = true;
	}

	@Override
	public void run() {
		while(stop == false) {
			// TODO check if we have any unblocking version of get
			RequestMessage message = reqQueue.poll();
			if(message == null || stop)
				return;

			executor.execute(dataSource, message);
		}
	}
}
