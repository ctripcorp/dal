package com.ctrip.platform.dal.cluster.sharding.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class MappedShardData implements ShardData {

    private final Map<String, Object> data;

    public MappedShardData(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }

    @Override
    public Object getValue(String name) {
        for (String key : data.keySet()) {
            if (key != null && key.equalsIgnoreCase(name))
                return data.get(key);
        }
        return null;
    }

    @Override
    public Set<String> getNames() {
        return data.keySet();
    }

}
