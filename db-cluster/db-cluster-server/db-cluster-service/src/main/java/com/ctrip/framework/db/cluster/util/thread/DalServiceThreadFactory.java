package com.ctrip.framework.db.cluster.util.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by @author zhuYongMing on 2019/11/1.
 */
public class DalServiceThreadFactory implements ThreadFactory {

    private final String title;

    private AtomicInteger threadIndex = new AtomicInteger();

    public DalServiceThreadFactory(String title) {
        this.title = title;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        String prefix = "DalService-";
        String name = String.format("%s-%s-%d", prefix, title, threadIndex.incrementAndGet());
        thread.setName(name);
        return thread;
    }
}
