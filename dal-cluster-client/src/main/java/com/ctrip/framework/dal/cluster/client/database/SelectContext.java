package com.ctrip.framework.dal.cluster.client.database;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class SelectContext {

    private String shardId;
    private boolean forceMaster = false;
    private boolean forceSlave = false;
    private Set<String> tags = new HashSet<>();

    public String getShardId() {
        return shardId;
    }

    public boolean isForceMaster() {
        return forceMaster;
    }

    public boolean isForceSlave() {
        return forceSlave;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setShardId(String shardId) {
        this.shardId = shardId;
    }

    public void setForceMaster(boolean forceMaster) {
        this.forceMaster = forceMaster;
    }

    public void setForceSlave(boolean forceSlave) {
        this.forceSlave = forceSlave;
    }

    public void addTags(String... tags) {
        this.tags.addAll(Arrays.asList(tags));
    }

}
