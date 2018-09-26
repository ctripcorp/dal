package com.ctrip.framework.idgen.server.config;

import org.junit.Test;
import qunar.tc.qconfig.client.QTable;

import java.util.Map;

public class TableConfigProviderTest {

    //@Test
    public void testGetConfig() {
        TableConfigProvider provider = new TableConfigProvider("cctest.t");
        QTable table = provider.getConfig();
        Map<String, String> row = table.row("key1");
        int i = 0;
        row = table.row("idgen_global_config");
        i++;
    }

}
