package com.ctrip.platform.dal.dao.datasource;

public interface DatasourceBackgroundExecutor {
    void execute(RefreshableDataSource dataSource);
}
