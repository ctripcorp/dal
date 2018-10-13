package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.constant.CatConstants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CtripWhitelist implements Whitelist<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripWhitelist.class);

    private static final String WHITELIST_ENABLED_FLAG = "on";
    private static final String SPLIT_SEPARATOR = ".";

    private AtomicReference<Set<String>> whitelist = new AtomicReference<Set<String>>(new HashSet<String>());

    public void load(Map<String, String> properties) {
        if (null == properties) {
            return;
        }
        Set<String> updated = new HashSet<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null && WHITELIST_ENABLED_FLAG.equalsIgnoreCase(value.trim())) {
                updated.add(key.trim().toLowerCase());
            }
        }
        Set<String> previous = whitelist.getAndSet(updated);
        compare(previous, updated);
    }

    public boolean validate(String name) {
        name = name.trim().toLowerCase();
        String[] splits = StringUtils.split(name, SPLIT_SEPARATOR);
        return internalValidate(splits);
    }

    private boolean internalValidate(String[] splits) {
        StringBuilder builder = new StringBuilder();
        for (String split : splits) {
            builder.append(split);
            if (internalValidate(builder.toString())) {
                return true;
            }
            builder.append(SPLIT_SEPARATOR);
        }
        return false;
    }

    private boolean internalValidate(String name) {
        return whitelist.get().contains(name);
    }

    private void compare(final Set<String> previous, final Set<String> updated) {
        // Added list
        Set<String> temp = new HashSet<>(updated);
        temp.removeAll(previous);
        if (!temp.isEmpty()) {
            String addedList = StringUtils.join(temp, ", ");
            String msg = String.format("Added whitelist: %s", addedList);
            LOGGER.info(msg);
            Cat.logEvent(CatConstants.TYPE_ROOT, CatConstants.NAME_WHITELIST_CHANGED,
                    Event.SUCCESS, msg);
        }
        // Removed list
        temp.clear();
        temp.addAll(previous);
        temp.removeAll(updated);
        if (!temp.isEmpty()) {
            String removedList = StringUtils.join(temp, ", ");
            String msg = String.format("Removed whitelist: %s", removedList);
            LOGGER.info(msg);
            Cat.logEvent(CatConstants.TYPE_ROOT, CatConstants.NAME_WHITELIST_CHANGED,
                    Event.SUCCESS, msg);
        }
    }

}
