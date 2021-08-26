package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.base.ComponentGenerator;
import com.ctrip.platform.dal.cluster.base.Listenable;

/**
 * @author c7ch23en
 */
public interface ClusterConfig extends ComponentGenerator<Cluster>, Listenable<ClusterConfig> {

    String getClusterName();

    boolean checkSwitchable(ClusterConfig newConfig);

}
