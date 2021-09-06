package com.ctrip.framework.dal.cluster.client.config;


import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.ComponentGenerator;
import com.ctrip.framework.dal.cluster.client.base.Listenable;

/**
 * @author c7ch23en
 */
public interface ClusterConfig extends ComponentGenerator<Cluster>, Listenable<ClusterConfig> {

    String getClusterName();

    boolean checkSwitchable(ClusterConfig newConfig);

}
