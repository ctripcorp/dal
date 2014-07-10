package com.ctrip.platform.dal.daogen.utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.entity.ExecuteResult;

public class TaskUtils {
	
	public static final int BATCH_SIZE = 50;
	
	private static ExecutorService executor = Executors.newCachedThreadPool();
	
	public static void invokeBatch(Logger log, List<Callable<ExecuteResult>> tasks) throws InterruptedException{
		int loop = tasks.size();
		for(int i = 0; i < loop; i += BATCH_SIZE){
			TaskUtils.log(log,
					executor.invokeAll(
							tasks.subList(i, Math.min(loop, i + BATCH_SIZE))
						)
					);
		}
	}
	
	public static void log(Logger log, List<Future<ExecuteResult>> tasks){
		for (Future<ExecuteResult> future : tasks) {
			try {
				ExecuteResult result = future.get();
				log.info(String.format("Execute [%s] task completed: %s", 
						result.getTaskName(), result.isSuccessal()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
//	public static void invokeBatch(Logger log, ExecutorService service, 
//			List<Callable<ExecuteResult>> tasks) throws InterruptedException{
//		int loop = tasks.size();
//		for(int i = 0; i < loop; i += BATCH_SIZE){
//			TaskUtils.log(log,
//					service.invokeAll(
//							tasks.subList(i, Math.min(loop, i + BATCH_SIZE))
//						)
//					);
//		}
//	}
}
