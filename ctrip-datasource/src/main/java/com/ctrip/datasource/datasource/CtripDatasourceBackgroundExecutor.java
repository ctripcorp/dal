package com.ctrip.datasource.datasource;

import com.ctrip.datasource.helper.DNS.CtripDatabaseDomainChecker;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DefaultDatasourceBackgroundExecutor;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.dao.datasource.SingleDataSourceWrapper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.DatabaseDomainChecker;
import com.ctrip.platform.dal.dao.log.ILogger;

public class CtripDatasourceBackgroundExecutor extends DefaultDatasourceBackgroundExecutor {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    DatabaseDomainChecker checker = new CtripDatabaseDomainChecker();

    @Override
    public void execute(SingleDataSourceWrapper dataSourceWrapper) {
        if (dataSourceWrapper == null)
            return;

        SingleDataSource singleDataSource = dataSourceWrapper.getSingleDataSource();
        if (singleDataSource == null)
            return;

        DataSourceConfigure configure = singleDataSource.getDataSourceConfigure();

        // start database domain checker
        startDatabaseDomainChecker(dataSourceWrapper);

        // start connection phantom reference cleaner
        super.startPhantomReferenceCleaner(configure);
    }

    private void startDatabaseDomainChecker(SingleDataSourceWrapper dataSourceWrapper) {
        try {
            checker.start(dataSourceWrapper);
        } catch (Throwable e) {
            LOGGER.error("An error occurred while starting CtripDatabaseDomainChecker.", e);
        }
    }

}
