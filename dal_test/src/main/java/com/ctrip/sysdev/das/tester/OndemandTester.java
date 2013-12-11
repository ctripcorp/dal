package com.ctrip.sysdev.das.tester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OndemandTester extends Thread {
	private ConcurrentLinkedQueue<String> queue;
	private DalClient dal = new DalClient("HtlProductdb");
	
	public OndemandTester(ConcurrentLinkedQueue<String> queue) {
		this.queue = queue;
	}
	
	@Override
	public void run() {
		long start = 0;
		while(true){
			String sql = queue.poll();
			if(sql == null){
				if(start != 0){
					System.out.println(this.getName() + " done: " + (System.currentTimeMillis() - start));
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
			new OndemandTester(queue).start();
		
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
	
	private static String logicDb;
	private static String sql;
	private static int threadNum;
	
	private static void readConfig() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("test_cfg.txt")));
			String cmd;
			cmd = reader.readLine();
			threadNum = Integer.parseInt(cmd);
			logicDb = reader.readLine();
			sql = reader.readLine();
			System.out.println("Thread number: " + threadNum);
			System.out.println("Logic DB: " + logicDb);
			System.out.println("Test SQL: " + sql);
			reader.close();
		} catch (IOException e1) {
			
		}
	}
}
