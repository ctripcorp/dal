package com.ctrip.platform.dal.application.fireman;

import com.ctrip.framework.fireman.spi.ForceSwitchDatasourceCallback;

public class DalForceSwitchDatasourceCallback implements ForceSwitchDatasourceCallback {
    @Override
    public void beforeForceSwitch() {

    }

    @Override
    public void afterForceSwitched() {

    }

    @Override
    public void beforeRestore() {

    }

    @Override
    public void afterRestored() {

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
