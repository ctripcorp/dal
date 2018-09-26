package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.idgen.server.exception.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CtripServer implements Server<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripServer.class);

    private static final String WORKERID_PROPERTY_KEY_FORMAT = "workerId_%s";
    private static final String WORKERID_PROPERTY_KEY_PATTERN = "workerId_*";

    private long workerId;

    public void initialize(Map<String, String> properties) {
        if (checkWorkerIdDuplication(properties)) {
            String msg = "[workerId] duplicated";
            LOGGER.error(msg);
            throw new InvalidParameterException(msg);
        }
        workerId = parseWorkerId(properties);
        if (workerId < 0) {
            String msg = "[workerId] should not be negative";
            LOGGER.error(msg);
            throw new InvalidParameterException(msg);
        }
    }

    private boolean checkWorkerIdDuplication(Map<String, String> properties) {
        if (null == properties) {
            return false;
        }
        Set<Long> workerIds = new HashSet<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null && Pattern.matches(WORKERID_PROPERTY_KEY_PATTERN, key.trim())) {
                try {
                    long workerId = Long.parseLong(value.trim());
                    if (!workerIds.add(workerId)) {
                        LOGGER.error("[workerId] duplicated ({}: {})", key, value);
                        return true;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return false;
    }

    private long parseWorkerId(Map<String, String> properties) {
        try {
            return Long.parseLong(properties.get(getWorkerIdPropertyKey()));
        } catch (Exception e) {
            LOGGER.error("[workerId] invalid", e);
            throw e;
        }
    }

    private String getWorkerIdPropertyKey() {
        return String.format(WORKERID_PROPERTY_KEY_FORMAT, getWorkerIdPropertyKeySuffix());
    }

    private String getWorkerIdPropertyKeySuffix() {
        try {
            return Foundation.net().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get local IP", e);
        }
    }

    @Override
    public long getWorkerId() {
        return workerId;
    }

}
