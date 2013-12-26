package com.ctrip.sysdev.das.tester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestHelper {
	private String CMD_CONFIG = "config";
	private String CMD_START = "start";
	private String CMD_END = "end";
	
//	private String CFG_THREAD = "thread";
//	private String CFG_LOGIC_DB = "logicDb";
//	private String CFG_THREAD = "thread";
//	private String CFG_THREAD = "thread";
	
	
	private int threadNum;
	private String logicDb;
	private int[] ports;
	private String sql;

	private ConcurrentLinkedQueue<String> queue;

	private List<DalClient> dalList = new ArrayList<DalClient>();

	public void config(int threadNum, String logicDb, int[] ports, String sql) {
		this.threadNum = threadNum;
		this.logicDb = logicDb;
		this.ports = ports;
		this.sql = sql;
		ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
		readConfig();

		int num = 0;
//		while (num++ < threadNum)
//			new OndemandTester(queue, logicDb, Integer.parseInt(ports[num
//					% ports.length])).start();

		System.out.println("Type end and return to terminate");

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String cmd;
			int number = 1;
			while (true) {
				cmd = reader.readLine();
				if ("end".equalsIgnoreCase(cmd.trim()))
					break;

				try {
					number = Math.abs(Integer.parseInt(cmd));
				} catch (NumberFormatException e) {
					System.out.println("Count: " + String.valueOf(number));
				}
				int i = 0;
				while (i++ < number)
					queue.offer(sql);

			}
		} catch (IOException e1) {
		}
		System.exit(0);
	}

	private static void readConfig() {
		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					new FileInputStream("test_cfg.txt")));
//			threadNum = Integer.parseInt(reader.readLine());
//			logicDb = reader.readLine();
//			ports = reader.readLine().split(",");
//			sql = reader.readLine();
//			System.out.println("Thread number: " + threadNum);
//			System.out.println("Logic DB: " + logicDb);
//			System.out.print("Ports: ");
//			for (String port : ports)
//				System.out.print(ports);
//			System.out.println();
//			System.out.println("Test SQL: " + sql);
//			reader.close();
		} catch (Throwable e1) {

		}
	}
}
