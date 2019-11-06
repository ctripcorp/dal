package com.ctrip.framework.db.cluster.fireman;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.fireman.spi.FiremanDependency;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.datasource.ForceSwitchableDataSource;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by @author zhuYongMing on 2019/11/3.
 */
public class DefaultFiremanDependency implements FiremanDependency {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ConnectionStringParser parser = ConnectionStringParser.getInstance();

    @Override
    public List<String> getAppIds() {
        return Lists.newArrayList(Constants.DAL_CLUSTER_SERVICE_APPID);
    }

    @Override
    public String getDatabaseDomainName() {
        String domainConnectionString = getDataSource().getSingleDataSource().getDataSourceConfigure().getConnectionString().getDomainConnectionString();
        return parser.parse(Constants.TITAN_KEY_NAME, domainConnectionString).getHostName();
    }

    @Override
    public ForceSwitchableDataSource getDataSource() {
        try {
            return (ForceSwitchableDataSource) new DalDataSourceFactory().createDataSource(Constants.TITAN_KEY_NAME, true);
        } catch (Exception e) {
            logger.error("fireman get dal forceSwitchableDatasource fail", e);
            throw new DalRuntimeException("get datasource error");
        }
    }

    @Override
    public int mhaSwitchMaxExecuteTimeoutS() {
        return 120;
    }

    @Override
    public boolean openAvailableCheckTask() {
        return true;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
