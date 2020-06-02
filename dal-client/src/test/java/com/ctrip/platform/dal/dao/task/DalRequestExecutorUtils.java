package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.configure.DalThreadPoolExecutorConfig;

import java.util.concurrent.ExecutorService;

/**
 * @author c7ch23en
 */
public class DalRequestExecutorUtils {

    public static ExecutorService getExecutor() {
        return DalRequestExecutor.getExecutor();
    }

    public static DalThreadPoolExecutorConfig getExecutorConfig(DalThreadPoolExecutor executor) {
        return executor.getExecutorConfig();
    }

}
