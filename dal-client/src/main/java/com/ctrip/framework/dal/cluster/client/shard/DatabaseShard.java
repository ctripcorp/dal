package com.ctrip.framework.dal.cluster.client.shard;

import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ReadStrategy;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface DatabaseShard {

    int getShardIndex();

    List<Database> getMasters();

    List<Database> getSlaves();

    ReadStrategy getRouteStrategy();

    Database selectDatabaseFromReadStrategy(DalHints dalHints);

}
