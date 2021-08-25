package com.ctrip.platform.dal.cluster.config;

import java.io.InputStream;

/**
 * @author c7ch23en
 */
public interface ClusterConfigParser {

    ClusterConfig parse(String content, DalConfigCustomizedOption customizedOption);

    ClusterConfig parse(InputStream stream, DalConfigCustomizedOption customizedOption);

}
