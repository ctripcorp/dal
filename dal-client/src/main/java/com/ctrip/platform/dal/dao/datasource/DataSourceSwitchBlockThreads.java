package com.ctrip.platform.dal.dao.datasource;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by taochen on 2019/10/9.
 */
public class DataSourceSwitchBlockThreads {
    private boolean needBlock = true;

    private Queue<Thread> blockThreads = new ConcurrentLinkedQueue<>();

    public boolean isNeedBlock() {
        return needBlock;
    }

    public void setNeedBlock(boolean needBlock) {
        this.needBlock = needBlock;
    }

    public void addBlockThread(Thread thread) {
        blockThreads.add(thread);
    }

    public Queue<Thread> getBlockThreads() {
        return blockThreads;
    }
}
