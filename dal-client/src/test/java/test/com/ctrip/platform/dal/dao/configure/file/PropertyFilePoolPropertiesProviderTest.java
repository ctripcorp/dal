package test.com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.file.PropertyFilePoolPropertiesProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class PropertyFilePoolPropertiesProviderTest implements DataSourceConfigureConstants {
    @Test
    public void testPropertyFilePoolPropertiesProvider() throws Exception {
        PropertyFilePoolPropertiesProvider provider = new PropertyFilePoolPropertiesProvider();
        Map<String, String> map = provider.getPoolProperties();
        // Custom value
        Assert.assertEquals(map.get(MINIDLE), "1");

        // Default value
        Assert.assertEquals(map.get(MAXACTIVE), String.valueOf(DEFAULT_MAXACTIVE));
    }
}
