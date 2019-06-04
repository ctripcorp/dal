package com.ctrip.platform.dal.dao.datasource;



public interface DataSourceCreatePoolListener {
    void onCreatePoolSuccess();
    void onCreatePoolFail(Throwable e);
}
