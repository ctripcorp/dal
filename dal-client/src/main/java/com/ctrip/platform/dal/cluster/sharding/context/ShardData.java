package com.ctrip.platform.dal.cluster.sharding.context;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface ShardData {

    Object getValue(String name);

    Set<String> getNames();

}
