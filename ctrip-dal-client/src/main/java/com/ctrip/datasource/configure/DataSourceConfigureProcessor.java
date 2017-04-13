package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigConstants;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConifg;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;

import static oracle.net.aso.C07.t;

public class DataSourceConfigureProcessor extends DatabasePoolConfigConstants {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigureProcessor.class);
    private static final String DAL_APPNAME = "FX.DAL";
    private static final String DAL_DATASOURCE_CONFIG = "FX.DAL.Datasource";
    private static DatabasePoolConifg poolConfig = null;

    static {
        setGlobalDataSourceConfigure();
    }

    private static void setGlobalDataSourceConfigure() {
        try {
            MapConfig config = MapConfig.get(DAL_APPNAME, DAL_DATASOURCE_CONFIG, null);
            if (config != null) {
                Map<String, String> datasource = config.asMap();
                poolConfig = new DatabasePoolConifg();
                PoolProperties prop = poolConfig.getPoolProperties();
                //Set PoolProperties
                String testWhileIdle = datasource.get(TESTWHILEIDLE);
                if (testWhileIdle != null)
                    prop.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));

                String testOnBorrow = datasource.get(TESTONBORROW);
                if (testOnBorrow != null)
                    prop.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));

                String testOnReturn = datasource.get(TESTONRETURN);
                if (testOnReturn != null)
                    prop.setTestOnReturn(Boolean.parseBoolean(testOnReturn));

                String validationQuery = datasource.get(VALIDATIONQUERY);
                if (validationQuery != null)
                    prop.setValidationQuery(validationQuery);

                String validationInterval = datasource.get(VALIDATIONINTERVAL);
                if (validationInterval != null)
                    prop.setValidationInterval(Long.parseLong(validationInterval));

                String timeBetweenEvictionRunsMillis = datasource.get(TIMEBETWEENEVICTIONRUNSMILLIS);
                if (timeBetweenEvictionRunsMillis != null)
                    prop.setTimeBetweenEvictionRunsMillis(Integer.parseInt(timeBetweenEvictionRunsMillis));

                String maxAge = datasource.get(MAX_AGE);
                if (maxAge != null)
                    prop.setMaxAge(Integer.parseInt(maxAge));

                String maxActive = datasource.get(MAXACTIVE);
                if (maxActive != null)
                    prop.setMaxActive(Integer.parseInt(maxActive));

                String minIdle = datasource.get(MINIDLE);
                if (minIdle != null)
                    prop.setMinIdle(Integer.parseInt(minIdle));

                String maxWait = datasource.get(MAXWAIT);
                if (maxWait != null)
                    prop.setMaxWait(Integer.parseInt(maxWait));

                String initialSize = datasource.get(INITIALSIZE);
                if (initialSize != null)
                    prop.setInitialSize(Integer.parseInt(initialSize));

                String removeAbandonedTimeout = datasource.get(REMOVEABANDONEDTIMEOUT);
                if (removeAbandonedTimeout != null)
                    prop.setRemoveAbandonedTimeout(Integer.parseInt(removeAbandonedTimeout));

                String removeAbandoned = datasource.get(REMOVEABANDONED);
                if (removeAbandoned != null)
                    prop.setRemoveAbandoned(Boolean.parseBoolean(removeAbandoned));

                String logAbandoned = datasource.get(LOGABANDONED);
                if (logAbandoned != null)
                    prop.setLogAbandoned(Boolean.parseBoolean(logAbandoned));

                String minEvictableIdleTimeMillis = datasource.get(MINEVICTABLEIDLETIMEMILLIS);
                if (minEvictableIdleTimeMillis != null)
                    prop.setMinEvictableIdleTimeMillis(Integer.parseInt(minEvictableIdleTimeMillis));

                String connectionProperties = datasource.get(CONNECTIONPROPERTIES);
                if (connectionProperties != null)
                    prop.setConnectionProperties(connectionProperties);

                String initSql = datasource.get(INIT_SQL);
                if (initSql != null)
                    prop.setInitSQL(initSql);

                String initSQL = datasource.get(INIT_SQL2);
                if (initSQL != null)
                    prop.setInitSQL(initSQL);
                //Option
                String option = datasource.get(OPTION);
                if (option != null)
                    poolConfig.setOption(option);
            }
        } catch (Throwable e) {
            logger.warn("setGlobalDataSourceConfigure error:" + e.getMessage());
        }
    }


}
