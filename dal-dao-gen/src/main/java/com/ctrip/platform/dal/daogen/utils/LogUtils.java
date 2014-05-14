package com.ctrip.platform.dal.daogen.utils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;

public class LogUtils {
	/**
	 * Log the execute results
	 * @param log
	 * @param tasks
	 */
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
}
