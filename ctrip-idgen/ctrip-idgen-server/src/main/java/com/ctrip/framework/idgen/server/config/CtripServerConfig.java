package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.idgen.server.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CtripServerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripServerConfig.class);
    private static final String WORKERID_PROPERTY_KEY_FORMAT = "workerId_%s";
    private static final String WORKERID_PROPERTY_KEY_PATTERN = "workerId_*";

    private long workerId;

    public void load(Map<String, String> config) {
        if (checkWorkerIdDuplication(config)) {
            String msg = "[workerId] duplicated";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
        workerId = parseWorkerId(config);
        if (workerId < 0) {
            String msg = "[workerId] should not be negative";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private long parseWorkerId(Map<String, String> config) {
        try {
            return PropertiesParser.parseLong(config, getWorkerIdPropertyKey().trim());
        } catch (Throwable t) {
            LOGGER.error("[workerId] invalid", t);
            throw t;
        }
    }

    private boolean checkWorkerIdDuplication(Map<String, String> config) {
        if (null == config) {
            return false;
        }
        Set<Long> workerIds = new HashSet<>();
        boolean result = false;
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null && Pattern.matches(WORKERID_PROPERTY_KEY_PATTERN, key.trim())) {
                try {
                    long workerId = Long.parseLong(value.trim());
                    if (!workerIds.add(workerId)) {
                        LOGGER.error("[workerId] duplicated ({}: {})", key, value);
                        result = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return result;
    }

    private String getWorkerIdPropertyKey() {
        return String.format(WORKERID_PROPERTY_KEY_FORMAT, getWorkerIdPropertyKeySuffix());
    }

    private String getWorkerIdPropertyKeySuffix() {
        try {
            return Foundation.net().getHostAddress();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get local IP", t);
        }
    }

    public long getWorkerId() {
        return workerId;
    }

}
