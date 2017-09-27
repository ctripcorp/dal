package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.entity.ExecuteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskUtils {
    private static ExecutorService executor =
            new ThreadPoolExecutor(20, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static void invokeBatch(List<Callable<ExecuteResult>> tasks) throws Exception {
        if (tasks == null || tasks.size() == 0)
            return;

        List<Future<ExecuteResult>> results = executor.invokeAll(tasks);
        List<String> exceptions = new ArrayList<>();
        for (Future<ExecuteResult> future : results) {
            try {
                ExecuteResult result = future.get();
            } catch (Throwable e) {
                exceptions.add(e.getMessage());
            }
        }

        if (exceptions.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String exception : exceptions) {
                sb.append(exception);
            }

            throw new RuntimeException(sb.toString());
        }
    }

}
