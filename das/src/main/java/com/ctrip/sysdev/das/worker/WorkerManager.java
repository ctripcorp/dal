package com.ctrip.sysdev.das.worker;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.ctrip.sysdev.das.domain.msg.Message;

/**
 * @deprecated
 * @author jhhe
 *
 */
public class WorkerManager {
	private DataSourceWrapper dataSource;

	private List<Worker> workers = new ArrayList<Worker>();
	private Queue<Message> reqQueue;

	public WorkerManager(DataSourceWrapper dataSource) {
		this(dataSource, Integer.MAX_VALUE, Runtime.getRuntime()
				.availableProcessors());
	}

	public WorkerManager(DataSourceWrapper dataSource, int capacity,
			int workerNum) {
		if (workerNum < 1)
			throw new IllegalArgumentException(
					"Number of workers must greater than 0.");

		this.dataSource = dataSource;

		// Use non-blocking queue for better performance
		reqQueue = new ConcurrentLinkedQueue<Message>();

		initWorkers(workerNum);
	}

	private void initWorkers(int workerNum) {
		workers = new ArrayList<Worker>();
		for (int i = 0; i < workerNum; i++) {
			workers.add(new Worker("DB-worker-" + i, dataSource, reqQueue));
		}
	}

	public boolean handle(Channel channel, Message msg) {
		return reqQueue.offer(msg);
	}

	/**
	 * TODO Shall we move initWorker here? First stop, then create new threads?
	 */
	public void start() {
		for (Thread worker : workers)
			worker.start();
	}

	public void stop() {
		for (Worker worker : workers)
			worker.setStopFlag();
	}
}
