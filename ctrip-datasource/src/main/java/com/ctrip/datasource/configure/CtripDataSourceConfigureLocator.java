package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.ConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.PropertiesWrapper;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import java.util.Map;
import java.util.Properties;

public class CtripDataSourceConfigureLocator extends DefaultDataSourceConfigureLocator {
    private static final String DAL = "DAL";
    private static final String POOLPROPERTIES_MERGEPOOLPROPERTIES_FORMAT = "PoolProperties::mergePoolProperties:%s";

    @Override
    public DataSourceConfigure mergeDataSourceConfigure(ConnectionString connectionString) {
        ConnectionStringConfigure connectionStringConfigure = getConnectionStringConfigure(connectionString);
        if (connectionStringConfigure == null)
            return null;

        String name = connectionStringConfigure.getName();
        DataSourceConfigure c = cloneDataSourceConfigure(null);
        String catName = String.format(POOLPROPERTIES_MERGEPOOLPROPERTIES_FORMAT, name);
        Transaction transaction = Cat.newTransaction(DAL, catName);

        try {
            PropertiesWrapper wrapper = propertiesWrapperReference.get();
            // override app-level properties
            Properties appProperties = wrapper.getAppProperties();
            if (appProperties != null && !appProperties.isEmpty()) {
                overrideProperties(c.getProperties(), appProperties);
                String log = "App 覆盖结果:" + poolPropertiesHelper.propertiesToString(c.getProperties());
                LOGGER.info(log);
                Cat.logEvent(DAL, catName, Message.SUCCESS, log);
            }

            // override datasource-level properties
            Map<String, Properties> datasourceProperties = wrapper.getDatasourceProperties();
            if (datasourceProperties != null && !datasourceProperties.isEmpty()) {
                if (name != null) {
                    Properties p1 = datasourceProperties.get(name);
                    if (p1 != null && !p1.isEmpty()) {
                        overrideProperties(c.getProperties(), p1);
                        String log = name + " 覆盖结果:" + poolPropertiesHelper.propertiesToString(c.getProperties());
                        LOGGER.info(log);
                        Cat.logEvent(DAL, catName, Message.SUCCESS, log);
                    } else {
                        String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
                        possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
                        Properties p2 = datasourceProperties.get(possibleName);
                        if (p2 != null && !p2.isEmpty()) {
                            overrideProperties(c.getProperties(), p2);
                            String log = possibleName + " 覆盖结果："
                                    + poolPropertiesHelper.propertiesToString(c.getProperties());
                            LOGGER.info(log);
                            Cat.logEvent(DAL, catName, Message.SUCCESS, log);
                        }
                    }
                }
            }

            // override config from connection settings,datasource.xml
            overrideConnectionStringConfigureAndDataSourceXml(connectionStringConfigure, c, connectionString, name);
            Cat.logEvent(DAL, catName, Message.SUCCESS,
                    String.format("最终覆盖结果：%s", poolPropertiesHelper.propertiesToString(c.toProperties())));
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            LOGGER.error(e.getMessage(), e);
        } finally {
            transaction.complete();
        }
        return c;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
