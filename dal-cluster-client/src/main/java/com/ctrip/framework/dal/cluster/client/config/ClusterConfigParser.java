package com.ctrip.framework.dal.cluster.client.config;

import java.io.InputStream;

/**
 * @author c7ch23en
 */
public interface ClusterConfigParser {

    ClusterConfig parse(String content);

    ClusterConfig parse(InputStream stream);

}
