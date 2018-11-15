package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.ConnectionPhantomReferenceCleaner;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.DefaultConnectionPhantomReferenceCleaner;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultDatasourceBackgroundExecutor implements DatasourceBackgroundExecutor {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    ConnectionPhantomReferenceCleaner cleaner = new DefaultConnectionPhantomReferenceCleaner();
    private static AtomicBoolean containsMySQL = new AtomicBoolean(false);

    @Override
    public void execute(RefreshableDataSource dataSource) {
        if (dataSource == null)
            return;

        SingleDataSource singleDataSource = dataSource.getSingleDataSource();
        if (singleDataSource == null)
            return;

        DataSourceConfigure configure = singleDataSource.getDataSourceConfigure();
        startPhantomReferenceCleaner(configure);
    }

    protected void startPhantomReferenceCleaner(DataSourceConfigure configure) {
        try {
            if (!containsMySQL.get()) {
                if (configure.getDatabaseCategory().equals(DatabaseCategory.MySql)) {
                    cleaner.start();
                    containsMySQL.set(true);
                }
            }
        } catch (Throwable e) {
            LOGGER.error(String.format("Error starting pool connectionPhantomReferenceCleaner"), e);
        }
    }

}
