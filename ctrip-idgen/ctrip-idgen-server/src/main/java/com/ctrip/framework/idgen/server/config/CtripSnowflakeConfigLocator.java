package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.constant.CatConstants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.QTable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CtripSnowflakeConfigLocator implements SnowflakeConfigLocator<QTable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripSnowflakeConfigLocator.class);

    private static final String GLOBAL_CONFIG_ROW_KEY = "idgen_global_config";
    private static final String CAT_NAME_SNOWFLAKE_CONFIG_CHANGED = "SnowflakeConfig.changed";

    private AtomicReference<SnowflakeConfig> globalConfigRef = new AtomicReference<>();
    private AtomicReference<Map<String, SnowflakeConfig>> sequenceConfigRef = new AtomicReference<>();
    private Server server;

    public CtripSnowflakeConfigLocator(Server server) {
        this.server = server;
    }

    public void setup(QTable config) {
        SnowflakeConfig globalConfig = new CtripSnowflakeConfig(server);
        if (null == config) {
            globalConfig.load(null);
        } else {
            globalConfig.load(config.row(GLOBAL_CONFIG_ROW_KEY));
        }
        globalConfigRef.set(globalConfig);

        if (config != null) {
            Map<String, Map<String, String>> rowMap = config.rowMap();
            if (rowMap != null) {
                Map<String, SnowflakeConfig> sequenceConfigMap = new HashMap<>();
                for (Map.Entry<String, Map<String, String>> entry : rowMap.entrySet()) {
                    String key = entry.getKey();
                    Map<String, String> properties = entry.getValue();
                    if (key != null && !GLOBAL_CONFIG_ROW_KEY.equalsIgnoreCase(key.trim())) {
                        SnowflakeConfig sequenceConfig = new CtripSnowflakeConfig(server, globalConfig);
                        sequenceConfig.load(properties);
                        sequenceConfigMap.put(key.trim().toLowerCase(), sequenceConfig);
                    }
                }
                sequenceConfigRef.set(sequenceConfigMap);
            }
        }
    }

    public SnowflakeConfig getSnowflakeConfig(String sequenceName) {
        SnowflakeConfig sequenceConfig = sequenceConfigRef.get().get(sequenceName.trim().toLowerCase());
        if (null == sequenceConfig) {
            sequenceConfig = globalConfigRef.get();
        }
        return sequenceConfig;
    }

    private void compare(final SnowflakeConfig previous, final SnowflakeConfig updated, String name) {
        if (null == previous && null == updated) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (null == previous) {
            builder.append(String.format("[Add config: %s] ", name));
            builder.append(updated.toString());
        } else if (null == updated) {
            builder.append(String.format("[Remove config: %s] ", name));
            builder.append(previous.toString());
        } else if (updated.diffs(previous)) {
            builder.append(String.format("[Update config: %s] ", name));
            builder.append(String.format("%s -> %s", previous.toString(), updated.toString()));
        }
        String message = builder.toString();
        LOGGER.info(message);
        Cat.logEvent(CatConstants.CAT_TYPE_IDGEN_SERVER, CAT_NAME_SNOWFLAKE_CONFIG_CHANGED,
                Event.SUCCESS, message);
    }

}
