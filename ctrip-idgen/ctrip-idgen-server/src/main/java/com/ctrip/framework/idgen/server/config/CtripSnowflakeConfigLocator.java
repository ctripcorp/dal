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
        SnowflakeConfig previous = globalConfigRef.getAndSet(globalConfig);
        compare(previous, globalConfig, GLOBAL_CONFIG_ROW_KEY);

        if (config != null) {
            Map<String, Map<String, String>> rowMap = config.rowMap();
            if (rowMap != null) {
                Map<String, SnowflakeConfig> sequenceConfigMap = new HashMap<>();
                for (Map.Entry<String, Map<String, String>> entry : rowMap.entrySet()) {
                    String key = entry.getKey();
                    Map<String, String> properties = entry.getValue();
                    if (key != null && !GLOBAL_CONFIG_ROW_KEY.equalsIgnoreCase(key.trim())) {
                        SnowflakeConfig sequenceConfig = new CtripSnowflakeConfig(server, globalConfigRef.get());
                        sequenceConfig.load(properties);
                        sequenceConfigMap.put(key.trim().toLowerCase(), sequenceConfig);
                    }
                }
                Map<String, SnowflakeConfig> previousMap = sequenceConfigRef.getAndSet(sequenceConfigMap);
                compare(previousMap, sequenceConfigMap);
            }
        }
    }

    public SnowflakeConfig getSnowflakeConfig(String sequenceName) {
        Map<String, SnowflakeConfig> sequenceConfigMap = sequenceConfigRef.get();
        if (sequenceConfigMap != null) {
            SnowflakeConfig sequenceConfig = sequenceConfigMap.get(sequenceName.trim().toLowerCase());
            if (sequenceConfig != null) {
                return sequenceConfig;
            }
        }
        return globalConfigRef.get();
    }

    private void compare(final SnowflakeConfig previous, final SnowflakeConfig updated, String name) {
        if (null == previous && null == updated) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (null == previous) {
            builder.append(String.format("Added snowflake config '%s': ", name));
            builder.append(updated.toString());
        } else if (null == updated) {
            builder.append(String.format("Removed snowflake config '%s': ", name));
            builder.append(previous.toString());
        } else if (updated.differs(previous)) {
            builder.append(String.format("Updated snowflake config '%s': ", name));
            builder.append(String.format("%s -> %s", previous.toString(), updated.toString()));
        }
        String message = builder.toString();
        LOGGER.info(message);
        Cat.logEvent(CatConstants.TYPE_ROOT, CatConstants.NAME_SNOWFLAKE_CONFIG_CHANGED,
                Event.SUCCESS, message);
    }

    private void compare(final Map<String, SnowflakeConfig> previous,
                         final Map<String, SnowflakeConfig> updated) {
        // Added and updated configs
        if (previous != null && updated != null) {
            for (Map.Entry<String, SnowflakeConfig> entry : updated.entrySet()) {
                compare(previous.get(entry.getKey()), entry.getValue(), entry.getKey());
            }
            // Removed configs
            for (Map.Entry<String, SnowflakeConfig> entry : previous.entrySet()) {
                if (!updated.containsKey(entry.getKey())) {
                    compare(entry.getValue(), null, entry.getKey());
                }
            }
        } else if (updated != null) {
            for (Map.Entry<String, SnowflakeConfig> entry : updated.entrySet()) {
                compare(null, entry.getValue(), entry.getKey());
            }
        } else if (previous != null) {
            for (Map.Entry<String, SnowflakeConfig> entry : previous.entrySet()) {
                compare(entry.getValue(), null, entry.getKey());
            }
        }
    }

}
