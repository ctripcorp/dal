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
    String USER_HINT_STRATEGY = "UserHintStrategy";
    String CUSTOM_STRATEGY = "CustomStrategy";
    String PROPERTY = "Property";
    String TABLES = "Tables";
    String TABLE = "Table";
    String ID_GENERATORS = "IdGenerators";
    String ID_GENERATOR = "IdGenerator";
    String ROUTE_STRATEGIES = "RouteStrategies";
    String ORDERED_ACCESS_STRATEGY = "OrderedAccessStrategy";
    String UNIT_STRATEGY_ID = "UnitStrategyId";
    String ZONE_ID = "ZoneId";

    // XML attributes
    String NAME = "name";
    String TYPE = "type";
    String DB_CATEGORY = "dbCategory";
    String VERSION = "version";
    String INDEX = "index";
    String MASTER_DOMAIN = "masterDomain";
    String MASTER_PORT = "masterPort";
    String MASTER_KEYS = "masterTitanKeys";
    String SLAVE_DOMAIN = "slaveDomain";
    String SLAVE_PORT = "slavePort";
    String SLAVE_KEYS = "slaveTitanKeys";
    String ROLE = "role";
    String IP = "ip";
    String PORT = "port";
    String DB_NAME = "dbName";
    String UID = "uid";
    String PWD = "pwd";
    String ZONE = "zone";
    String READ_WEIGHT = "readWeight";
    String TAGS = "tags";
    String VALUE = "value";
    String DEFAULT = "default";
    String CLASS = "class";

}
