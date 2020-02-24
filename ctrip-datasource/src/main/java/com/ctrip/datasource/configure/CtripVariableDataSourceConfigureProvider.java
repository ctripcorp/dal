package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.EnvUtil;
import com.ctrip.datasource.util.VariableConnectionStringUtils;
import com.ctrip.datasource.util.entity.VariableConnectionStringInfo;
import com.ctrip.platform.dal.dao.configure.AbstractVariableDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CtripVariableDataSourceConfigureProvider extends AbstractVariableDataSourceConfigureProvider {

    private static final String DB_TOKEN_FILE = "db_token.properties";
    private Map<String, String> tokenCache = new ConcurrentHashMap<>();

    private DataSourceConfigureManager dataSourceConfigureManager = DataSourceConfigureManager.getInstance();
    private DalPropertiesManager dalPropertiesManager = DalPropertiesManager.getInstance();


    @Override
    public Map<String, DalConnectionStringConfigure> getConnectionStrings(Set<String> dbNames) throws UnsupportedEncodingException {
        String env = EnvUtil.getEnv();
        Map<String, DalConnectionStringConfigure> dalConnectionStringConfigures = new HashMap<>();
        for (String dbName : dbNames) {
            VariableConnectionStringInfo info = VariableConnectionStringUtils.getConnectionStringFromDBAPI(dbName, env);
            dalConnectionStringConfigures.put(dbName, VariableConnectionStringParser.parser(dbName, info, tokenCache.get(dbName)));
        }
        return dalConnectionStringConfigures;
    }

    @Override
    public void setup(Set<String> dbNames) {
        dalPropertiesManager.setup();
        dataSourceConfigureManager.setup(dbNames);
        initialize();
    }

    private void initialize() {
        MapConfig config = MapConfig.get(DB_TOKEN_FILE, Feature.create().setHttpsEnable(true).build());
        config.asMap();
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> conf) {
                changeToken(conf);
            }
        });
    }

    private void changeToken(Map<String, String> properties) {
        tokenCache = properties;
    }
}
