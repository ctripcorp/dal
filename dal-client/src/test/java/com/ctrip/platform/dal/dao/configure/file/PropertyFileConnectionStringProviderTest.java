package com.ctrip.platform.dal.dao.configure.file;


import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropertyFileConnectionStringProviderTest implements DataSourceConfigureConstants {
    private static final String NAME = "test";
    private static final String COMMA = ",";

    @Test
    public void testPropertyFileConnectionStringProvider() throws Exception {
        PropertyFileConnectionStringProvider provider = new PropertyFileConnectionStringProvider();
        Set<String> names = new HashSet<>();
        names.add(NAME);
        Map<String, DalConnectionString> map = provider.getConnectionStrings(names);
        DalConnectionString connectionString = map.get(NAME);
        String cs = connectionString.getIPConnectionString();
        String[] array = cs.split(COMMA);

        Assert.assertEquals(array[0], "testUser");
        Assert.assertEquals(array[1], "testPassword");
        Assert.assertEquals(array[2], "jdbc:mysql://testIP:3306/testDb");
        Assert.assertEquals(array[3], "com.mysql.jdbc.Driver");
    }
}
