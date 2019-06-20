package com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
import org.junit.Assert;
import org.junit.Test;

public class PropertyFilePoolPropertiesProviderTest implements DataSourceConfigureConstants {
    @Test
    public void testPropertyFilePoolPropertiesProvider() throws Exception {
        PropertyFilePoolPropertiesProvider provider = new PropertyFilePoolPropertiesProvider();
        PoolPropertiesConfigure configure = provider.getPoolProperties();
        // Custom value
        Assert.assertEquals(configure.getMinIdle(), "1");

        // Default value
        Assert.assertEquals(configure.getMaxActive(), String.valueOf(DEFAULT_MAXACTIVE));
    }
}
