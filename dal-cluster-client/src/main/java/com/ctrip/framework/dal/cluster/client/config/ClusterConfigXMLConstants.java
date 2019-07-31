package com.ctrip.framework.dal.cluster.client.config;

/**
 * @author c7ch23en
 */
public interface ClusterConfigXMLConstants {

    // XML elements
    String DAL = "DAL";
    String CLUSTER = "Cluster";
    String DATABASE_SHARDS = "DatabaseShards";
    String DATABASE_SHARD = "DatabaseShard";
    String DATABASE = "Database";
    String SHARD_STRATEGIES = "ShardStrategies";
    String MOD_STRATEGY = "ModStrategy";
    String CUSTOM_STRATEGY = "CustomStrategy";
    String PROPERTY = "Property";
    String TABLES = "Tables";
    String TABLE = "Table";

    // XML attributes
    String NAME = "name";
    String DB_CATEGORY = "dbCategory";
    String VERSION = "version";
    String INDEX = "index";
    String MASTER_DOMAIN = "masterDomain";
    String MASTER_PORT = "masterPort";
    String MASTER_KEY = "masterKey";
    String SLAVE_DOMAIN = "slaveDomain";
    String SLAVE_PORT = "slavePort";
    String SLAVE_KEY = "slaveKey";
    String ROLE = "role";
    String IP = "ip";
    String PORT = "port";
    String DB_NAME = "dbName";
    String UID = "uid";
    String PWD = "pwd";
    String READ_WEIGHT = "readWeight";
    String TAGS = "tags";
    String VALUE = "value";
    String DEFAULT = "default";
    String CLASS = "class";

}
