package com.ctrip.sysdev.das.tester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StressTestManager extends Thread {
	private List<StressTester> testers = new ArrayList<StressTester>();
	private int clientCount;
	private int duration;
	private String host;
	private String logicDb;
	private String[] ports;
	private String sql;

	private int port;
	
	public StressTestManager(int port) {
		this.port = port;
	}
	
	public long getDuration() {
		return duration;
	}
	
	
	private void test() {
		readDbConfig();
		readTesterConfig();
		startTesters();
		waitForDuration();
		stopTesters();
		report();
	}
	
	private void readDbConfig() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("stress.conf")));
			host = reader.readLine();
			logicDb = reader.readLine();
			sql = reader.readLine();

			System.out.println("Logic DB: " + logicDb);
			System.out.println("Test SQL: " + sql);
			
			System.out.println("DAS ports: ");
			ports = reader.readLine().split(",");

			int i = 0;
			for(String port: ports)
				System.out.print(String.format("Port %d:%s ", i++, port));

			System.out.println();
			duration = Math.abs(Integer.parseInt(reader.readLine()));
			System.out.println("Test duration(in second): " + duration);
			duration *= 1000;

			reader.close();
		} catch (IOException e) {
		}
	}
	
	private void readTesterConfig() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.print("Dal client count: ");
			clientCount = Math.abs(Integer.parseInt(reader.readLine()));
		} catch (IOException e) {
		}
	}
	
	
	private void startTesters() {
		System.out.println("Create testers...");
		for(int i = 0; i < clientCount; i++){
			StressTester tester = new StressTester(host, logicDb, Integer.parseInt(ports[i%ports.length]), sql);
			testers.add(tester); 
			tester.start();
		}
		System.out.println(String.format("All %d testers started", testers.size()));
	}
	
	private void waitForDuration() {
		synchronized (this){
			try {
				this.wait(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void stopTesters() {
		for(StressTester tester: testers)
			tester.stopTest();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void report() {
		int i = 0;
		long totalReq = 0;
		long totalDuration = 0;
		System.out.println("Test finished.");
		System.out.println("ID\tReq\tDura\tAvr\tTps");
		double totalTps = 0.0d;
		for(StressTester tester: testers) {
			long count = tester.getCount();
			long duration = tester.getDuration();
			double tps = count/(duration/1000d);
			System.out.println(String.format("%d\t%d\t%d\t%f\t%f", i++, count, duration, (double)duration/(double)count, tps));
			totalReq += tester.getCount();
			totalDuration += tester.getDuration();
			totalTps += tps;
		}
		
		System.out.println("Total");
		System.out.println("Req\tDura\tAvr\tTps");
		System.out.println(String.format("%d\t%d\t%f\t%f", totalReq, totalDuration, (double)totalDuration/(double)totalReq, totalTps));
	}
	
	public static void main(String[] args) {
		StressTestManager testManager = new StressTestManager(1);
		testManager.test();
	}
}
