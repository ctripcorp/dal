package com.ctrip.platform.dal.tester;

public class StressTester extends Thread {
	private String host;
	private String logicDbName;
	private int port;
	private String sql;
	private volatile boolean stop;
	private long duration;
	private long count;
	
	public StressTester(String host, String logicDbName, int port, String sql) {
		this.host = host;
		this.logicDbName = logicDbName; 
		this.port = port;
		this.sql = sql;
	}
	
	public void run() {
		DalClient dal = new DalClient(host, logicDbName, port);
		long start = System.currentTimeMillis();
		while(!stop) {
			dal.executeQuery(sql);
			count++;
		}
		duration = System.currentTimeMillis() - start;
	}
	
	public void stopTest() {
		stop = true;
	}
	
	public long getCount() {
		return this.count;
	}
	
	public long getDuration() {
		return this.duration;
	}
}
