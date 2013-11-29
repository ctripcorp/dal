package com.ctrip.sysdev.das.tester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OndemandTester extends Thread {
	private ConcurrentLinkedQueue<String> queue;
	private DalClient dal = new DalClient();
	
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
		int dalClientCount = 1;
		try {
			dalClientCount = Math.abs(Integer.parseInt(args[0]));
		} catch (NumberFormatException e) {
		}
		int num = 0;
		while(num++ < dalClientCount)
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
				long start = System.currentTimeMillis();
				while(i++ < number)
					queue.offer("select * from Person");
				System.out.println("Duration: " + (System.currentTimeMillis() - start));
				
			}
		} catch (IOException e1) {
		}
		System.exit(0);
	}
}
