package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.datasource.SingleDataSourceWrapper;

public interface DatabaseDomainChecker {
    void start(SingleDataSourceWrapper dataSourceWrapper);
}
