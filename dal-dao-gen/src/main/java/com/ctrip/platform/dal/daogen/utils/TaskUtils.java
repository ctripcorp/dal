package com.ctrip.platform.dal.daogen.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TaskUtils {
	public static final int BATCH_SIZE = 50;
	
	public static <T> List<Future<T>> invokeBatch(ExecutorService service, 
			List<? extends Callable<T>> tasks) throws InterruptedException{
		List<Future<T>> result = new ArrayList<Future<T>>();
		int loop = tasks.size();
		for(int i = 0; i < loop; i += BATCH_SIZE){
			result.addAll(service.invokeAll(
					tasks.subList(i, Math.min(loop, i + BATCH_SIZE))));
		}
		return result;
	}
}
