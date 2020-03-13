package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.EnvUtil;
import com.ctrip.datasource.util.MysqlApiConnectionStringUtils;
import com.ctrip.datasource.util.entity.ClusterNodeInfo;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.dianping.cat.Cat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MysqlApiConnectionStringConfigureProvider implements ConnectionStringConfigureProvider, DataSourceConfigureConstants {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final int THREAD_SIZE = 1;
    private static final String THREAD_NAME = "DAL-MysqlApiConnectionStringChecker";
    private static final int INITIAL_DELAY = 0;
    private static final int FIXED_DELAY = 3 * 1000; //ms
    private static final String SEPARATED = ",";
    private static final String LOAD_BALANCED_JDBC_URL_PARAMETER = "&%s&%s";
    private static final String URL_PARAMETER = "%s=%s";
    private static final String IP_PORT = "%s:%s";
    private static final String SERVER_AFFINITY_ORDER_FORMAT = "address=(type=master)(protocol=tcp)(host=%s)(port=%s):3306";
    private static final String[] IDC_ACCESS_ORDER = new String[] {"shaoy", "sharb", "shafq", "shajq"};
    private static final String CONNECTION_STRING_CHECKER_EVENT_NAME = "connectionStringChecker";
    private static final String CONNECTION_STRING_TYPE = "dal.connectionString";

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
    }

    private DalPoolPropertiesConfigure getMysqlApiConfigureProperties(PropertiesWrapper propertiesWrapper) {
        Properties appProperties = propertiesWrapper.getAppProperties();
        Map<String, Properties> datasourcePropertiesMap = propertiesWrapper.getDatasourceProperties();
        Properties dataSourceProperties = datasourcePropertiesMap.get(dbName);

        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        if (dataSourceProperties == null) {
            if (StringUtils.isEmpty(appProperties.getProperty(DB_TOKEN))) {
                throw new DalRuntimeException(String.format("the db token of %s is null", dbName));
            }

            dataSourceConfigure.setProperties(appProperties);
            return dataSourceConfigure;
        }
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

        dataSourceConfigure.setProperties(mysqlApiConfigure);
        return dataSourceConfigure;
    }

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        DalConnectionStringConfigure dalConnectionStringConfigure = null;

        try {
            dalConnectionStringConfigure = getConnectionStringFromMysqlApi();
        } catch (Exception e) {
            dalConnectionStringConfigure = new InvalidVariableConnectionString(dbName, new DalException(e.getMessage(), e));
        }

        return dalConnectionStringConfigure;
    }

    protected DalConnectionStringConfigure getConnectionStringFromMysqlApi() throws Exception {
        initMysqlApiConfigure();
        String env = EnvUtil.getEnv();
        MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(mysqlApiUrl, dbName, env);

        DataSourceConfigure connectionStringConfigure = MysqlApiConnectionStringParser.getInstance().parser(dbName, info, dbToken, dbModel);

        addMGRLocalToLocalParam(connectionStringConfigure, info.getClusternodeinfolist());
        return connectionStringConfigure;
    }

    private void addMGRLocalToLocalParam(DataSourceConfigure connectionStringConfigure, List<ClusterNodeInfo> clusterNodeInfos) {
        String connectionUrl = connectionStringConfigure.getConnectionUrl();
        if (connectionUrl.startsWith(DatabaseCategory.REPLICATION_MYSQL_JDBC_URL_PREFIX)) {
            String urlParam1 = String.format(URL_PARAMETER, LOAD_BALANCE_STRATEGY, DEFAULT_LOAD_BALANCE_STRATEGY);
            String urlParam2 = String.format(URL_PARAMETER, SERVER_AFFINITY_ORDER, getServerAffinityOrder(clusterNodeInfos));
            String urlPostfix = String.format(LOAD_BALANCED_JDBC_URL_PARAMETER, urlParam1, urlParam2);

            String loadBalanceUrl = connectionStringConfigure.getConnectionUrl() + urlPostfix;
            connectionStringConfigure.setConnectionUrl(loadBalanceUrl);
        }
    }

    private String getServerAffinityOrder(List<ClusterNodeInfo> clusterNodeInfos) {
        String currentIdc = EnvUtil.getIdc().toLowerCase();

        String serverAffinityOrder = "";

        Map<String, String> idcAndIpPort = new HashMap<>();
        for (ClusterNodeInfo clusterNodeInfo : clusterNodeInfos) {
            String ipPort = String.format(SERVER_AFFINITY_ORDER_FORMAT, clusterNodeInfo.getIp_business(), clusterNodeInfo.getDns_port());
            idcAndIpPort.put(clusterNodeInfo.getMachine_located_short().toLowerCase(), ipPort);
        }

        String ipPortInCurrentIdc = idcAndIpPort.get(currentIdc);
        if (!StringUtils.isEmpty(ipPortInCurrentIdc)) {
            serverAffinityOrder += ipPortInCurrentIdc + SEPARATED;
        }

        for (String idc : IDC_ACCESS_ORDER) {
            if (idc.equalsIgnoreCase(currentIdc)) {
                continue;
            }
            String ipPort = idcAndIpPort.get(idc);
            if (StringUtils.isEmpty(ipPort)) {
                continue;
            }
            serverAffinityOrder += ipPort + SEPARATED;
        }

        return serverAffinityOrder.substring(0, serverAffinityOrder.length() - 1);
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {
        this.listener = listener;

        executor = Executors.newScheduledThreadPool(THREAD_SIZE, new CustomThreadFactory(THREAD_NAME));
        executor.scheduleWithFixedDelay(new MysqlApiConnectionStringChecker(), INITIAL_DELAY, callMysqlApiPeriod, TimeUnit.MILLISECONDS);
        LOGGER.logEvent(CONNECTION_STRING_TYPE, CONNECTION_STRING_CHECKER_EVENT_NAME, String.valueOf(callMysqlApiPeriod));
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
