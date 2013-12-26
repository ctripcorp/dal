package com.ctrip.sysdev.das.tester;

public class StressTester2 extends Thread {
	private String host;
	private String logicDbName;
	private int port;
	private String sql;
	private volatile boolean stop;
	private long duration;
	private long nanoDuration;
	private long count;
	private long nanoDelay;
	
	public StressTester2(String host, String logicDbName, int port, String sql, long nanoDelay) {
		this.host = host;
		this.logicDbName = logicDbName; 
		this.port = port;
		this.sql = sql;
		this.nanoDelay = nanoDelay;
	}
	
	public void run() {
		DalClient dal = new DalClient(host, logicDbName, port);
		
		long nanoStart = 0;
		long delay = 0;
		double c;
		long start = System.currentTimeMillis();
		while(!stop) {
			nanoDelay = System.nanoTime();
			dal.executeQuery(sql);
			nanoDuration += (System.nanoTime() - start);
			count++;
			// Delay
			delay = System.nanoTime();
			while((System.nanoTime() - delay) < nanoDelay){
				c = delay + duration/count;
				c--;
			}
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
		return duration;
	}
	
	public long getART() {
		return (nanoDuration/count);
	}
}
