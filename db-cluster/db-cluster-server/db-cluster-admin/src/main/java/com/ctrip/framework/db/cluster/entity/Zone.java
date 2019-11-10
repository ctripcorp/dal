package com.ctrip.framework.db.cluster.entity;

import java.util.List;

/**
 * Created by taochen on 2019/11/6.
 */
public class Zone {
    private String zoneId;

    private List<Shard> shards;

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public List<Shard> getShards() {
        return shards;
    }

    public void setShards(List<Shard> shards) {
        this.shards = shards;
    }
}
