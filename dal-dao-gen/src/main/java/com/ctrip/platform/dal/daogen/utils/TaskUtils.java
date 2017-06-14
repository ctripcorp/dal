package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;

public class TaskUtils {
    private static Logger logger = Logger.getLogger(TaskUtils.class);
    private static ExecutorService executor =
            new ThreadPoolExecutor(20, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static void invokeBatch(Logger log, List<Callable<ExecuteResult>> tasks) throws Exception {
        try {
            TaskUtils.log(log, executor.invokeAll(tasks));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    public static void log(Logger log, List<Future<ExecuteResult>> tasks) throws Exception {
        for (Future<ExecuteResult> future : tasks) {
            try {
                ExecuteResult result = future.get();
                log.info(String.format("Execute [%s] task completed: %s", result.getTaskName(), result.isSuccessal()));
            } catch (Throwable e) {
                LoggerManager.getInstance().error(e);
                throw e;
            }
        }
    }

}
