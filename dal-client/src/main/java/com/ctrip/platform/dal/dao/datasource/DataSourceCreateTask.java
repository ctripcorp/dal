package com.ctrip.platform.dal.dao.datasource;


public interface DataSourceCreateTask extends Runnable {
    void cancel();
}
