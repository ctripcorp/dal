package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.log.LoggerManager;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

public class TaskUtils {
    private static ExecutorService executor =
            new ThreadPoolExecutor(20, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static Map<Integer, String> errorMap = new TreeMap<>();

    public static void addError(Integer taskId, String errorMessage) {
        errorMap.put(taskId, errorMessage);
    }

    private static void clearMap() {
        errorMap = null;
        errorMap = new TreeMap<>();
    }

    public static void invokeBatch(List<Callable<ExecuteResult>> tasks) throws Exception {
        try {
            executor.invokeAll(tasks);

            if (errorMap != null && errorMap.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Integer, String> entry : errorMap.entrySet()) {
                    sb.append(String.format("Task Id[%s]:%s\r\n", entry.getKey(), entry.getValue()));
                }

                clearMap();
                throw new Exception(sb.toString());
            }
        } catch (Throwable e) {
            throw e;
        }
    }

}
