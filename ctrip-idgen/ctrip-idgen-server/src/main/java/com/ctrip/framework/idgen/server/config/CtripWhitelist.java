package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CtripWhitelist implements Whitelist<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripWhitelist.class);

    private static final String WHITELIST_ENABLED_FLAG = "on";
    private AtomicReference<Set<String>> whitelist = new AtomicReference<>();

    public CtripWhitelist() {
        whitelist.set(new HashSet<String>());
    }

    public void load(Map<String, String> properties) {
        if (null == properties) {
            return;
        }

        Set<String> enabledList = new HashSet<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null && WHITELIST_ENABLED_FLAG.equalsIgnoreCase(value.trim())) {
                enabledList.add(key.trim());
            }
        }

        // Parse removed list
        Set<String> current = whitelist.get();
        Set<String> tempSet = new HashSet<>(current);
        tempSet.removeAll(enabledList);
        if (!tempSet.isEmpty()) {
            String removedList = StringUtils.setToString(tempSet, ", ");
            LOGGER.info("Removed from whitelist: {}", removedList);
        }

        // Parse added list
        tempSet.clear();
        tempSet.addAll(enabledList);
        tempSet.removeAll(current);
        if (!tempSet.isEmpty()) {
            String addedList = StringUtils.setToString(tempSet, ", ");
            LOGGER.info("Added to whitelist: {}", addedList);
        }

        whitelist.set(enabledList);
    }

    public boolean validate(String name) {
        Set<String> current = whitelist.get();
        return name != null && current.contains(name.trim());
    }

}
