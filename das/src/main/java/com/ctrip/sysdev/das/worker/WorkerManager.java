package com.ctrip.sysdev.das.worker;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.sysdev.das.domain.msg.Message;

/*
 * One manager will handles one request queue. The Netty entry code will responsible for 
 * the initial dispatch. 
 */
public class WorkerManager {
	private String logicDbName;
	// private ExecutorService executor;  
	private ConnectionPool connPool;
	
	// Max length of the queue. Not used for now
	private int capacity;
	
	private int workerNum;

	private List<Worker> workers = new ArrayList<Worker>();
	
	private Queue<Message> reqQueue;
	
	public WorkerManager(String logicDbName) {
		this(logicDbName, Integer.MAX_VALUE, Runtime.getRuntime().availableProcessors());
	}
	
	public WorkerManager(String logicDbName, int capacity, int workerNum) {
		if(workerNum < 1)
			throw new IllegalArgumentException("Number of workers must greater than 0.");
		
		this.logicDbName = logicDbName;
		this.capacity = capacity;
		this.workerNum = workerNum;
		
		connPool = new ConnectionPool(logicDbName);
		
		// Use non-blocking queue for better performance
		reqQueue = new ConcurrentLinkedQueue<Message>();
		
		initWorkers(workerNum);
	}
	
	private void initWorkers(int workerNum) {
		// executor = Executors.newFixedThreadPool(workerNum); 
		workers = new ArrayList<Worker>();
		for(int i = 0 ; i < workerNum; i++) {
			workers.add(new Worker(logicDbName + "-worker-" + i, connPool, reqQueue));
		}
	}
	
	public boolean handle(Channel channel, Message msg) {
		return reqQueue.offer(msg);
	}
	
	/**
	 * TODO Shall we move initWorker here? First stop, then create new threads?
	 */
	public void start() {
		for(Thread worker: workers)
			worker.start();
	}
	
	public void stop() {
		for(Worker worker: workers)
			worker.setStopFlag();
	}
}
