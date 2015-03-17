package com.ctrip.platform.dal.daogen.utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.entity.ExecuteResult;

public class TaskUtils {
	
	private static Logger logger = Logger.getLogger(TaskUtils.class);
	
	private static ExecutorService executor = new ThreadPoolExecutor(20,
            50,
            120,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
	
	public static void invokeBatch(Logger log, List<Callable<ExecuteResult>> tasks) {
		try {
			TaskUtils.log( log, executor.invokeAll(tasks) );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void log(Logger log, List<Future<ExecuteResult>> tasks){
		for (Future<ExecuteResult> future : tasks) {
			try {
				ExecuteResult result = future.get();
				log.info(String.format("Execute [%s] task completed: %s", 
						result.getTaskName(), result.isSuccessal()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
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
