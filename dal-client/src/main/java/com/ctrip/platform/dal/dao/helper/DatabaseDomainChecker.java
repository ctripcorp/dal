package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;

public interface DatabaseDomainChecker {
    void start(RefreshableDataSource dataSource);
}
