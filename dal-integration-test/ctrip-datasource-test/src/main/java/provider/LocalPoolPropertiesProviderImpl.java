package provider;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;

import java.util.Properties;

public class LocalPoolPropertiesProviderImpl extends AbstractPoolPropertiesProvider
        implements DataSourceConfigureConstants {
    private PoolPropertiesChanged callback = null;

    public void triggerPoolPropertiesChanged() {
        Properties p = getProperties();
        p.setProperty(ENABLE_DYNAMIC_POOL_PROPERTIES, "true");
        p.setProperty(MINIDLE, "1");
        DataSourceConfigure configure = new DataSourceConfigure("", p);
        callback.onChanged(configure);
    }

    @Override
    public void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback) {
        if (callback == null)
            return;

        this.callback = callback;
    }

}
