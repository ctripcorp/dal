package com.ctrip.framework.db.cluster.util.thread;

import java.util.concurrent.ExecutorService;

/**
 * Created by @author zhuYongMing on 2019/11/1.
 */
public interface ExecutorFactory {

    ExecutorService createExecutorService();
}
