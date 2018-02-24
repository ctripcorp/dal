package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocalConnectionStringProvider extends AbstractConnectionStringProvider
        implements DataSourceConfigureConstants {
    private String connectionString2 =
            "Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;version=2";

    private String connectionString2Failover =
            "Server=10.32.21.149;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;version=2";

    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private long delay = 5 * 1000;

    @Override
    public void addConnectionStringChangedListener(String name, final ConnectionStringChanged callback) {
        if (callback == null)
            return;

        // emulate dynamic switching
        service.schedule(new Runnable() {
            @Override
            public void run() {
                Map<String, String> map = new HashMap<>();
                map.put(TITAN_KEY_NORMAL, connectionString2);
                map.put(TITAN_KEY_FAILOVER, connectionString2Failover);
                callback.onChanged(map);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

}
