package test.com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.file.PropertyFileConnectionStringProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropertyFileConnectionStringProviderTest implements DataSourceConfigureConstants {
    private static final String NAME = "test";

    @Test
    public void testPropertyFileConnectionStringProvider() throws Exception {
        PropertyFileConnectionStringProvider provider = new PropertyFileConnectionStringProvider();
        Set<String> names = new HashSet<>();
        names.add(NAME);
        Map<String, DataSourceConfigure> map = provider.getConnectionStrings(names);
        DataSourceConfigure configure = map.get(NAME);

        Assert.assertEquals(configure.getUserName(), "testUser");
        Assert.assertEquals(configure.getPassword(), "testPassword");
        Assert.assertEquals(configure.getConnectionUrl(), "jdbc:mysql://testIP:3306/testDb");
        Assert.assertEquals(configure.getDriverClass(), "com.mysql.jdbc.Driver");
    }
}
