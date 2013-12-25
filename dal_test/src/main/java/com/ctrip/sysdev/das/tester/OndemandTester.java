package com.ctrip.sysdev.das.tester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OndemandTester extends Thread {
	private ConcurrentLinkedQueue<String> queue;
	private DalClient dal;
	private boolean done;
	private long duration;
	
	public OndemandTester(ConcurrentLinkedQueue<String> queue) {
		this.queue = queue;
	}
	
	public OndemandTester init(String logicDbName, int port) {
		dal = new DalClient(logicDbName, port);
		return this;
	}
	
	public void done() {
		done = true;
	}
	
	public long getDuration() {
		return duration;
	}
	
	@Override
	public void run() {
		long start = 0;
		while(!done){
			String sql = queue.poll();
			if(sql == null){
				if(start != 0){
					System.out.println(this.getName() + " done: " + (duration = System.currentTimeMillis() - start));
					start = 0;
				}
				yield();
				continue;
			}
			if(start == 0)
				start = System.currentTimeMillis();
			dal.fetch(sql, null, null);
		}
	}
	
	public static void main(String[] args) {
		ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
		readConfig();
		
		int num = 0;
		while(num++ < threadNum)
			new OndemandTester(queue).init(logicDb, Integer.parseInt(ports[num%ports.length])).start();
		
		System.out.println("Type end and return to terminate");
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String cmd;
			int number = 1;
			while(true){
				cmd = reader.readLine();
				if("end".equalsIgnoreCase(cmd.trim()))
					break;

				try {
					number = Math.abs(Integer.parseInt(cmd));
				} catch (NumberFormatException e) {
					System.out.println("Count: " + String.valueOf(number));
				}
				int i = 0;
				while(i++ < number)
					queue.offer(sql);
				
			}
		} catch (IOException e1) {
		}
		System.exit(0);
	}
	
	private static int threadNum;
	private static String logicDb;
	private static String[] ports;
	private static String sql;
	
	private static void readConfig() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("test_cfg.txt")));
			threadNum = Integer.parseInt(reader.readLine());
			logicDb = reader.readLine();
			ports = reader.readLine().split(",");
			sql = reader.readLine();
			System.out.println("Thread number: " + threadNum);
			System.out.println("Logic DB: " + logicDb);
			System.out.print("Ports: ");
			for(String port: ports)
				System.out.print(port);
			System.out.println();
			System.out.println("Test SQL: " + sql);
			reader.close();
		} catch (IOException e1) {
			
		}
	}
}
