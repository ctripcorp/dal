package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;

import java.util.Map;

public class LocalPoolPropertiesProvider extends AbstractPoolPropertiesProvider
        implements DataSourceConfigureConstants {
    private PoolPropertiesChanged callback = null;

    public void triggerPoolPropertiesChanged() {
        Map<String, String> map = getMap();
        map.put(ENABLE_DYNAMIC_POOL_PROPERTIES, "true");
        map.put(MINIDLE, "1");
        callback.onChanged(map);
    }

    @Override
    public void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback) {
        if (callback == null)
            return;

        this.callback = callback;
    }

}
