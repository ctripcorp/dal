package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.datasource.SingleDataSource;

public class DefaultDatabaseDomainChecker implements DatabaseDomainChecker {
    @Override
    public void startCheckingTask(String name, SingleDataSource dataSource) {}
}
