package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.SwitchableDataSourceStatus;

public class MockSwitchListener implements IForceSwitchableDataSource.SwitchListener {
    private String onCallMethodName;
    private Throwable e;

    public String getOnCallMethodName() {
        return onCallMethodName;
    }

    public Throwable getThrowable(){
        return e;
    }

    @Override
    public void onForceSwitchSuccess(SwitchableDataSourceStatus currentStatus) {
        onCallMethodName = "onForceSwitchSuccess";
    }

    @Override
    public void onForceSwitchFail(SwitchableDataSourceStatus currentStatus, Throwable cause) {
        onCallMethodName = "onForceSwitchFail";
        e = cause;
    }

    @Override
    public void onRestoreSuccess(SwitchableDataSourceStatus currentStatus) {
        onCallMethodName = "onRestoreSuccess";
    }

    @Override
    public void onRestoreFail(SwitchableDataSourceStatus currentStatus, Throwable cause) {
        onCallMethodName = "onRestoreFail";
        e = cause;
    }
}
