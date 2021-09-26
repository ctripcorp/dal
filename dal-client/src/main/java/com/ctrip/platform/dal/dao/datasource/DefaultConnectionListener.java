package com.ctrip.platform.dal.dao.datasource;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

public class DefaultConnectionListener extends AbstractConnectionListener implements ConnectionListener {

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
