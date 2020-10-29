package com.ctrip.platform.dal.application.fireman;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.framework.fireman.spi.FiremanDependency;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.application.utils.Constants;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.datasource.ForceSwitchableDataSource;
import com.ctrip.platform.dal.dao.datasource.IForceSwitchableDataSource;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.google.common.collect.Lists;

import java.util.List;

public class DalFiremanDependency implements FiremanDependency {
    private static final String KEY_NAME = "dalservice2db_fork";
    private ConnectionStringParser parser = ConnectionStringParser.getInstance();

    @Override
    public List<String> getAppIds() {
        return Lists.newArrayList(Constants.APPID);
    }

    @Override
    public String getDatabaseDomainName() {
        return Constants.DB_NAME;
//        String domainConnectionString = getDataSource().getSingleDataSource().getDataSourceConfigure().getConnectionString().getDomainConnectionString();
//        return parser.parse(KEY_NAME, domainConnectionString).getHostName();
    }

    @Override
    public ForceSwitchableDataSource getDataSource() {
        try {
            return new WrappedForceSwitchableDataSource((IForceSwitchableDataSource) new DalDataSourceFactory().getOrCreateDataSource(KEY_NAME, true));
        } catch (Exception e) {
            throw new DalRuntimeException("get datasource error");
        }
    }

    @Override
    public int mhaSwitchMaxExecuteTimeoutS() {
        return 120;
    }

    @Override
    public boolean openAvailableCheckTask() {
        return false;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
