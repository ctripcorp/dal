package com.ctrip.sysdev.das.worker;

import java.sql.Connection;
import java.util.Queue;

import com.ctrip.sysdev.das.handler.RequestDispatcher;
import com.ctrip.sysdev.das.msg.Message;

public class Worker extends Thread {
	private String name;
	private boolean stop;
	private ConnectionPool connPool;
	private Queue<Message> reqQueue;
	private RequestDispatcher dispatcher;
	
	public Worker(
			String name, 
			ConnectionPool connPool, 
			Queue<Message> reqQueue) {
		super(name);
		this.connPool = connPool;
		this.reqQueue = reqQueue;
		dispatcher = new RequestDispatcher();
	}
	
	public void setStopFlag() {
		stop = true;
	}

	@Override
	public void run() {
		while(stop == false) {
			Message request = reqQueue.poll();
			if(request == null || stop)
				return;

			Connection conn = connPool.getConnection();
			if(conn == null || stop)
				return;
			
			processRequest(request);
		}
	}
	
	/**
	 * Note: we should first get request, then get connection
	 */
	private void processRequest(Message request) {
		try {
			dispatcher.dispatch(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Do fancy stuff here
	}
	
	private void process(Connection conn) {
		try {
			
		} catch(Throwable e) {
			
		} finally {
			// Return connection and do clean up
			connPool.returnConnection(conn);
		}
	}

	private void select() {

	}

	private void insert() {

	}

	private void delete() {

	}
	
	private void update() {

	}
}
