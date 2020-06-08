package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;

import java.util.HashMap;
import java.util.Map;

public class LocalConnectionStringProvider extends AbstractConnectionStringProvider
        implements DataSourceConfigureConstants {
    private String connectionString2 =
            "Server=10.32.20.128;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;version=2";

    private String connectionString2Failover =
            "Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;version=2";

    private ConnectionStringChanged callback = null;

    public void triggerConnectionStringChanged() {
        Map<String, String> map = new HashMap<>();
        map.put(TITAN_KEY_NORMAL, connectionString2);
        map.put(TITAN_KEY_FAILOVER, connectionString2Failover);
        DalConnectionString connectionString = new ConnectionString("", connectionString2, connectionString2Failover);

        callback.onChanged(connectionString);
    }

    @Override
    public void addConnectionStringChangedListener(String name, final ConnectionStringChanged callback) {
        if (callback == null)
            return;

        this.callback = callback;
    }

}
