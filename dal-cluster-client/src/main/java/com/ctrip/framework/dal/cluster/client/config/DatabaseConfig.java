package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.database.Database;

/**
 * @author c7ch23en
 */
public interface DatabaseConfig {

    Database generateDatabase();

}
