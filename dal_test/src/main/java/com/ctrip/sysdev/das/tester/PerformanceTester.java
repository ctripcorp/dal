package com.ctrip.sysdev.das.tester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PerformanceTester implements Runnable {
	private List<SqlTask> tasks = new ArrayList<SqlTask>();
	private ExecutorService executor;
	
	public PerformanceTester(DalClient dal, String[] sqls) {
		laod(sqls, dal);
		executor = Executors.newSingleThreadExecutor();
		System.out.println("Task count: " + tasks.size());
	}
	
	public void laod(String[] sqls, DalClient dal) {
		for(String sql: sqls) {
			if(sql.trim().length() == 0)
				continue;
			tasks.add(new SqlTask(sql, dal));
		}
	}
	
	private static DalClient[] buildClients(int size) {
		DalClient[] dals = new DalClient[3];
		dals[0] = new DalClient();
		dals[1] = new DalClient();
		dals[2] = new DalClient();
		return dals;
	}
	
	public void run() {
		for(SqlTask task: tasks)
			executor.execute(task);
		System.out.println(String.format("%s   %d tasks added", Thread.currentThread().getName(), tasks.size()));
	}
	
	public void shutdown() {
		executor.shutdown();
	}
	
	public static void main(String[] args) {
		SqlRepository rep = new SqlRepository();
		String[][] repos = rep.getSqlReps();
		DalClient[] dals = buildClients(repos.length);
		ScheduledExecutorService[] executors = new ScheduledExecutorService[repos.length];
		PerformanceTester[] testers = new PerformanceTester[repos.length];
		for(int i = 0; i < repos.length; i++) {
			ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();;
			PerformanceTester p = new PerformanceTester(dals[i], repos[i]);
			testers[i] = p;
			// Delay every 5 second
			e.scheduleAtFixedRate(p, i*5, 1, TimeUnit.SECONDS);
			executors[i] = e;
		}
		
		System.out.println("Press any key to terminate");
		try {
			System.in.read();
		} catch (IOException e1) {
		}
		
		for(ScheduledExecutorService e: executors)
			e.shutdown();

		for(PerformanceTester tester: testers)
			tester.shutdown();
	}
}
