package com.ctrip.sysdev.das.worker;

import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.ctrip.sysdev.das.domain.RequestMessage;

public class QueryExecutorManager {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ExecutorService executor;  
	private DataSourceWrapper dataSource;
	
	public QueryExecutorManager(DataSourceWrapper dataSource) {
		this(dataSource, Integer.MAX_VALUE, Runtime.getRuntime().availableProcessors());
	}
	
	public QueryExecutorManager(DataSourceWrapper dataSource, int capacity, int workerNum) {
		if(workerNum < 1)
			throw new IllegalArgumentException("Number of workers must greater than 0.");
		
		this.dataSource = dataSource;		
		executor = Executors.newFixedThreadPool(workerNum);
	}
	
	public boolean handle(Channel channel, RequestMessage message) {
		executor.submit(QueryExecutorWrapper.wrap(dataSource, message));
		return true;
	}
	
	public void stop() {
		try {
			executor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.equals(e);
		}
	}
}
