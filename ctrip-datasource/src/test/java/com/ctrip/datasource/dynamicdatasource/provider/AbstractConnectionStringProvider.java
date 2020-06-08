package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbstractConnectionStringProvider implements ConnectionStringProvider {
    private String connectionString1 =
            "Server=10.32.20.128;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_0;version=1";

    private String connectionString1Failover =
            "Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_0;version=1";

    @Override
    public Map<String, DalConnectionString> getConnectionStrings(Set<String> names) throws Exception {
        if (names == null || names.size() == 0)
            return null;

        Map<String, DalConnectionString> map = new HashMap<>();
        for (String name : names) {
            DalConnectionString connectionString =
                    new ConnectionString(name, connectionString1, connectionString1Failover);
            map.put(name, connectionString);
        }

        return map;
    }

    @Override
    public void addConnectionStringChangedListener(String name, ConnectionStringChanged callback) {}

}
