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

public class MysqlApiConnectionStringConfigureProvider implements ConnectionStringConfigureProvider {

    private String dbName;
    private DalPropertiesLocator dalPropertiesLocator;
    private DataSourceConfigureLocator dataSourceConfigureLocator;

    private String mysqlApiUrl;
    private String dbToken;
    private int callMysqlApiPeriod;
    private DBModel dbModel;

    public MysqlApiConnectionStringConfigureProvider(String dbName) {
        this.dbName = dbName;
        dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
        dataSourceConfigureLocator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();
    }

    public void initMysqlApiConfigure() {
        mysqlApiUrl = dalPropertiesLocator.getConnectionStringMysqlApiUrl();

        DalPoolPropertiesConfigure poolProperties = dataSourceConfigureLocator.getDataSourceConfigure(new ApiDataSourceIdentity(this));
        dbToken = poolProperties.getDBToken();
        callMysqlApiPeriod = poolProperties.getCallMysqlApiPeriod();
        dbModel = poolProperties.getDBModel();
    }

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        String env = EnvUtil.getEnv();
        DalConnectionStringConfigure dalConnectionStringConfigure = null;

        try {
            MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(dbName, env, dbModel);
            dalConnectionStringConfigure = MysqlApiConnectionStringParser.getInstance().parser(dbName, info, dbToken);
        } catch (Exception e) {
            dalConnectionStringConfigure = new InvalidVariableConnectionString(dbName, new DalException(e.getMessage(), e));
        }

        return dalConnectionStringConfigure;
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {

    }
}
