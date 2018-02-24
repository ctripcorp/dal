package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocalPoolPropertiesProvider extends AbstractPoolPropertiesProvider
        implements DataSourceConfigureConstants {
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private long delay = 5 * 1000;

    @Override
    public void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback) {
        if (callback == null)
            return;

        // emulate dynamic switching
        service.schedule(new Runnable() {
            @Override
            public void run() {
                Map<String, String> map = getMap();
                map.put(ENABLE_DYNAMIC_POOL_PROPERTIES, "true");
                map.put(MINIDLE, "1");
                callback.onChanged(map);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

}
