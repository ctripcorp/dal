package com.ctrip.platform.dal.application.fireman;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.framework.fireman.spi.FiremanDependency;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.datasource.ForceSwitchableDataSource;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class DalFiremanDependency implements FiremanDependency {
    private static final String KEY_NAME = "DalService2DB_W";
    private ConnectionStringParser parser = ConnectionStringParser.getInstance();

    @Override
    public List<String> getAppIds() {
        List<String> appIds = new ArrayList<>();
        appIds.add(Foundation.app().getAppId());
        return appIds;
    }

    @Override
    public String getDatabaseDomainName() {
        String domainConnectionString = getDataSource().getSingleDataSource().getDataSourceConfigure().getConnectionString().getDomainConnectionString();
        return parser.parse(KEY_NAME, domainConnectionString).getHostName();
    }

    @Override
    public ForceSwitchableDataSource getDataSource() {
        ForceSwitchableDataSource dataSource = null;
        try {
            dataSource = (ForceSwitchableDataSource) new DalDataSourceFactory().createDataSource(KEY_NAME);
        } catch (Exception e) {
            throw new DalRuntimeException("get datasource error");
        }
        return dataSource;
    }

    @Override
    public int mhaSwitchMaxExecuteTimeoutS() {
        return 20;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
