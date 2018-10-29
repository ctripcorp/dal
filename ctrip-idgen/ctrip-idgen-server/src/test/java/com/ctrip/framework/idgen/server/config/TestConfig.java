package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.foundation.Foundation;

import java.util.HashMap;
import java.util.Map;

public class TestConfig {

/*    public static ServerConfig mockServerConfig() {
        Map<String, String> map = new HashMap<>();
        String localhost = Foundation.net().getHostAddress();
        map.put("workerId_" + localhost, "1");
        ServerConfig config = new CtripServerConfig();
        config.importConfig(map);
        return config;
    }*/

    public static Server mockServer() {
        Server config = new CtripServer();
//        ((CtripServer) config).workerId = 1;
        return config;
    }

    public static SnowflakeConfig mockConfig() {
        SnowflakeConfig config = new CtripSnowflakeConfig(mockServer());
        config.load(null);
        return config;
    }

}
