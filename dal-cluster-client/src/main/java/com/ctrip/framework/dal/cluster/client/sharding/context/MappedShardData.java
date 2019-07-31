package com.ctrip.framework.dal.cluster.client.sharding.context;

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
        return data.get(name);
    }

    @Override
    public Set<String> getNames() {
        return data.keySet();
    }

}
