package com.ctrip.platform.dal.dao.datasource;

/**
 * Created by taochen on 2019/8/8.
 */
public class MockDataSourceSwitchListenerOne implements DataSourceSwitchListener {
    private volatile int step = 1;

    private int sleep = 100;
    @Override
    public void preHandle() {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            return;
        }
        step = 10;
    }

    public int getStep() {
        return step;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }
}
