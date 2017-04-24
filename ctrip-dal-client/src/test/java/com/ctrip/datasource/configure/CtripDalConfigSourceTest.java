package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import org.junit.Test;

import java.util.Map;

public class CtripDalConfigSourceTest {
    @Test
    public void testGetDatabaseSets() {
        CtripDalConfigSource configSource = new CtripDalConfigSource();
        try {
            Map<String, DatabaseSet> map = configSource.getDatabaseSets(null);
        } catch (Throwable e) {

        }
    }
}
