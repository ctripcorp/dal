package com.ctrip.platform.dal.dao.datasource;

/**
 * Created by taochen on 2019/8/8.
 */
public class MockDataSourceSwitchListenerTwo implements DataSourceSwitchListener {
    private volatile int step = 1;

    private int sleep = 150;

    private boolean isEnd = false;
    @Override
    public void execute() {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            return;
        }
        step = 20;
        isEnd = true;
    }

    public int getStep() {
        return step;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public boolean getEnd() {
        return isEnd;
    }

    public void resetEnd() {
        isEnd = false;
    }
}
