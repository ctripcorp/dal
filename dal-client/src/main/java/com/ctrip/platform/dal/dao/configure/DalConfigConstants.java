package com.ctrip.platform.dal.dao.configure;

public interface DalConfigConstants {
    String DAL_XML = "dal.xml";
    String DAL_CONFIG = "Dal.config";

    String NAME = "name";
    String DATABASE_SETS = "databaseSets";
    String IGNORE_RESOURCE_NOT_FOUND = "ignoreResourceNotFound";
    String DATABASE_SET = "databaseSet";
    String CLUSTER = "cluster";
    String ALIAS = "alias";
    String ADD = "add";
    String PROVIDER = "provider";
    String SHARD_STRATEGY = "shardStrategy";
    String SHARDING_STRATEGY = "shardingStrategy";
    String DATABASE_TYPE = "databaseType";
    String SHARDING = "sharding";
    String CONNECTION_STRING_PROVIDER = "connectionStringProvider";
    String CONNECTION_STRING = "connectionString";
    String MASTER = "Master";
    String LOG_LISTENER = "LogListener";
    String TASK_FACTORY = "TaskFactory";
    String FACTORY = "factory";
    String LOGGER = "logger";
    String SETTINGS = "settings";
    String CONNECTION_LOCATOR = "ConnectionLocator";
    String LOCATOR = "locator";
    String DATABASE_SELECTOR = "DatabaseSelector";
    String SELECTOR = "selector";
    String CONSISTENCY_TYPE_CUSTOMIZED_CLASS = "consistencyTypeCustomizedClass";
    String ROUTE_STRATEGY = "routeStrategy";
    String TAG = "tag";
    String QUERY_CONSISTENT = "queryConsistent";

    String ID_GENERATOR = "idGenerator";
    String ID_GENERATOR_FACTORY = "factory";
    String SEQUENCE_DATABASE_NAME = "sequenceDbName";
    String ENTITY_DATABASE_NAME = "entityDbName";
    String ENTITY_PACKAGE = "entityPackage";
    String INCLUDES = "includes";
    String INCLUDE = "include";
    String EXCLUDES = "excludes";
    String EXCLUDE = "exclude";
    String TABLES = "tables";
    String TABLE = "table";
}