package com.ctrip.framework.idgen.server.constant;

public interface CatConstants {

    String TYPE_ROOT = "IdGen.Server";

    String NAME_QCONFIG_LOAD = "QConfig.load";
    String NAME_QCONFIG_RELOAD = "QConfig.reload";
    String NAME_WHITELIST_CHANGED = "Whitelist.changed";
    String NAME_SNOWFLAKE_CONFIG_CHANGED = "SnowflakeConfig.changed";
    String NAME_WORKER_CREATED = "Worker.created";
    String NAME_WORKER_TIMEOUT = "Worker.timeout";

    String STATUS_WARN = "WARN";
    String STATUS_NULL_CONFIG = "NULL_CONFIG";

}
