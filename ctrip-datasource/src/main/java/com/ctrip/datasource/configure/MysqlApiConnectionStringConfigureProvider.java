package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.EnvUtil;
import com.ctrip.datasource.util.MysqlApiConnectionStringUtils;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DalPoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.InvalidVariableConnectionString;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.exceptions.DalException;

import java.util.concurrent.atomic.AtomicBoolean;

public class MysqlApiConnectionStringConfigureProvider implements ConnectionStringConfigureProvider {

    private String dbName;
    private DalPropertiesLocator dalPropertiesLocator;
    private DataSourceConfigureLocator dataSourceConfigureLocator;

    private String mysqlApiUrl;
    private String dbToken;
    private int callMysqlApiPeriod;
    private DBModel dbModel;

    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    public MysqlApiConnectionStringConfigureProvider(String dbName) {
        this.dbName = dbName;
        dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
        dataSourceConfigureLocator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();
    }

    private void initMysqlApiConfigure() {
        if (!isInitialized.get()) {
            synchronized (isInitialized) {
                if (!isInitialized.get()) {
                    mysqlApiUrl = dalPropertiesLocator.getConnectionStringMysqlApiUrl();

                    DalPoolPropertiesConfigure poolProperties = dataSourceConfigureLocator.getDataSourceConfigure(new ApiDataSourceIdentity(this));
                    dbToken = poolProperties.getDBToken();
                    callMysqlApiPeriod = poolProperties.getCallMysqlApiPeriod();
                    dbModel = poolProperties.getDBModel();
                    isInitialized.set(true);
                }
            }
        }
    }

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        initMysqlApiConfigure();
        String env = EnvUtil.getEnv();
        DalConnectionStringConfigure dalConnectionStringConfigure = null;

        try {
            MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(mysqlApiUrl, dbName, env);
            dalConnectionStringConfigure = MysqlApiConnectionStringParser.getInstance().parser(dbName, info, dbToken, dbModel);
        } catch (Exception e) {
            dalConnectionStringConfigure = new InvalidVariableConnectionString(dbName, new DalException(e.getMessage(), e));
        }

        return dalConnectionStringConfigure;
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {

    }
}
