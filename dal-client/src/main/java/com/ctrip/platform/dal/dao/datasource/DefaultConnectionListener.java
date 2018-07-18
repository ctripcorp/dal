package com.ctrip.platform.dal.dao.datasource;

public class DefaultConnectionListener extends AbstractConnectionListener implements ConnectionListener {

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
