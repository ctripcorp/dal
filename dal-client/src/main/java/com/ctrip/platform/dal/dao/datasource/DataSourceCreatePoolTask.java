package com.ctrip.platform.dal.dao.datasource;

/**
 * @author c7ch23en
 */
public interface DataSourceCreatePoolTask extends Runnable {
    void cancelTask();
}
