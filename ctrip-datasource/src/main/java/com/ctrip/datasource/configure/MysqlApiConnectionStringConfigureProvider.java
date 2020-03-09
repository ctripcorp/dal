package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.EnvUtil;
import com.ctrip.datasource.util.MysqlApiConnectionStringUtils;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.dianping.cat.Cat;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MysqlApiConnectionStringConfigureProvider implements ConnectionStringConfigureProvider, DataSourceConfigureConstants {

    private static final int THREAD_SIZE = 1;
    private static final String THREAD_NAME = "DAL-MysqlApiConnectionStringChecker";
    private static final int INITIAL_DELAY = 0;
    private static final int FIXED_DELAY = 3 * 1000; //ms

    private String dbName;
    private DalPropertiesLocator dalPropertiesLocator;
    private DataSourceConfigureLocator dataSourceConfigureLocator;

    private Listener<DalConnectionStringConfigure> listener;

    private String mysqlApiUrl;
    private String dbToken;
    private int callMysqlApiPeriod;
    private DBModel dbModel;

    private ScheduledExecutorService executor;

    public MysqlApiConnectionStringConfigureProvider(String dbName) {
        this.dbName = dbName;
        dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
        dataSourceConfigureLocator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();
    }

    private void initMysqlApiConfigure() {
        mysqlApiUrl = dalPropertiesLocator.getConnectionStringMysqlApiUrl();

        PropertiesWrapper propertiesWrapper = dataSourceConfigureLocator.getPoolProperties();
        DalPoolPropertiesConfigure mysqlApiConfigureProperties = getMysqlApiConfigureProperties(propertiesWrapper);

        dbToken = mysqlApiConfigureProperties.getDBToken();
        callMysqlApiPeriod = mysqlApiConfigureProperties.getCallMysqlApiPeriod();
        dbModel = mysqlApiConfigureProperties.getDBModel();

        executor = Executors.newScheduledThreadPool(THREAD_SIZE, new CustomThreadFactory(THREAD_NAME));
        executor.scheduleWithFixedDelay(new MysqlApiConnectionStringChecker(), INITIAL_DELAY, callMysqlApiPeriod, TimeUnit.MILLISECONDS);
    }

    private DalPoolPropertiesConfigure getMysqlApiConfigureProperties(PropertiesWrapper propertiesWrapper) {
        Properties appProperties = propertiesWrapper.getAppProperties();
        Map<String, Properties> datasourcePropertiesMap = propertiesWrapper.getDatasourceProperties();
        Properties dataSourceProperties = datasourcePropertiesMap.get(dbName);

        Properties mysqlApiConfigure = new Properties();

        String dbToken = dataSourceProperties.getProperty(DB_TOKEN) != null ? dataSourceProperties.getProperty(DB_TOKEN) : appProperties.getProperty(DB_TOKEN);
        if (StringUtils.isEmpty(dbToken)) {
            throw new DalRuntimeException(String.format("the db token of %s is null", dbName));
        }
        mysqlApiConfigure.setProperty(DB_TOKEN, dbToken);

        String callMysqlApiPeriod = dataSourceProperties.getProperty(CALL_MYSQL_API_PERIOD) != null ? dataSourceProperties.getProperty(CALL_MYSQL_API_PERIOD)
                : appProperties.getProperty(CALL_MYSQL_API_PERIOD);
        if (StringUtils.isEmpty(callMysqlApiPeriod)) {
            callMysqlApiPeriod = String.valueOf(FIXED_DELAY);
        }
        mysqlApiConfigure.setProperty(CALL_MYSQL_API_PERIOD, callMysqlApiPeriod);

        String dbModel = dataSourceProperties.getProperty(DB_MODEL) != null ? dataSourceProperties.getProperty(DB_MODEL) : appProperties.getProperty(DB_MODEL);
        if (StringUtils.isEmpty(dbModel)) {
            dbModel = DBModel.STANDALONE.getName();
        }
        mysqlApiConfigure.setProperty(DB_MODEL, dbModel);

        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setProperties(mysqlApiConfigure);

        return dataSourceConfigure;
    }

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        initMysqlApiConfigure();

        DalConnectionStringConfigure dalConnectionStringConfigure = null;

        try {
            dalConnectionStringConfigure = getConnectionStringFromMysqlApi();
        } catch (Exception e) {
            dalConnectionStringConfigure = new InvalidVariableConnectionString(dbName, new DalException(e.getMessage(), e));
        }

        return dalConnectionStringConfigure;
    }

    private DalConnectionStringConfigure getConnectionStringFromMysqlApi() throws Exception {
        String env = EnvUtil.getEnv();
        MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(mysqlApiUrl, dbName, env);

        return MysqlApiConnectionStringParser.getInstance().parser(dbName, info, dbToken, dbModel);
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {
        this.listener = listener;
    }

    private class MysqlApiConnectionStringChecker implements Runnable {

        @Override
        public void run() {
            DalConnectionStringConfigure dalConnectionStringConfigure = null;

            try {
                dalConnectionStringConfigure = getConnectionStringFromMysqlApi();
            } catch (Exception e) {
                Cat.logError("timed get connection string from mysql api failed!", e);
            }
            listener.onChanged(dalConnectionStringConfigure);
        }
    }
}
