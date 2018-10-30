package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.datasource.SingleDataSource;

public interface DatabaseDomainChecker {
    void startCheckingTask(String name, SingleDataSource dataSource);
}
