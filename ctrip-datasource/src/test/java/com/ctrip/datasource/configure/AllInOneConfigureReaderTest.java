package com.ctrip.datasource.configure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.datasource.titan.TitanProvider;
import org.junit.Assert;
import org.junit.Test;

public class AllInOneConfigureReaderTest {
    @Test
    public void testGetDataSourceConfiguresSuccess() {
        AllInOneConfigureReader reader = new AllInOneConfigureReader();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");
        dbNames.add("SimpleShard_1");
        dbNames.add("dao_test_sqlsvr");
        dbNames.add("dao_test_mysql");
        dbNames.add("PayBaseDB_INSERT_2");
    }

    @Test
    public void testGetDataSourceConfiguresValidateFail() {
        AllInOneConfigureReader reader = new AllInOneConfigureReader();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");
        dbNames.add("SimpleShard_1");
        dbNames.add("dao_test_sqlsvr");
        dbNames.add("dao_test_mysql");
        dbNames.add("test");
        try {
            reader.getDataSourceConfigures(dbNames, true, null);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetAllInOneConfig() throws Exception {
        TitanProvider provider = new TitanProvider();
        Map<String, String> settings = new HashMap<>();
        settings.put(TitanProvider.USE_LOCAL_CONFIG, "true");
        settings.put(TitanProvider.DATABASE_CONFIG_LOCATION, "$classpath");
        provider.initialize(settings);
        Set<String> names = new HashSet<>();
        names.add("SimpleShard_0");
        provider.setup(names);
    }
}
