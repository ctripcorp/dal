package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbstractConnectionStringProvider implements ConnectionStringProvider {
    private String connectionString1 =
            "Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_0;version=1";

    private String connectionString1Failover =
            "Server=10.32.21.149;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_0;version=1";

    @Override
    public Map<String, DataSourceConfigure> getConnectionStrings(Set<String> dbNames) throws Exception {
        if (dbNames == null || dbNames.size() == 0)
            return null;

        Map<String, DataSourceConfigure> map = new HashMap<>();
        for (String dbName : dbNames) {
            DataSourceConfigure configure = ConnectionStringParser.getInstance().parse(dbName, connectionString1);
            ConnectionString connectionString = new ConnectionString(connectionString1, connectionString1Failover);
            configure.setConnectionString(connectionString);
            map.put(dbName, configure);
        }

        return map;
    }

    @Override
    public void addConnectionStringChangedListener(String name, ConnectionStringChanged callback) {}

}
