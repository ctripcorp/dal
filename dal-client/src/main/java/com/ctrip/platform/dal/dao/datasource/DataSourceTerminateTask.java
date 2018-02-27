package com.ctrip.platform.dal.dao.datasource;

import java.util.concurrent.ScheduledExecutorService;

public interface DataSourceTerminateTask extends Runnable {
    void setScheduledExecutorService(ScheduledExecutorService service);
}
