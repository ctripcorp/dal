package com.ctrip.platform.dal.sharding.idgen;

public interface IIdGeneratorConfig {

    IdGenerator getIdGenerator(String tableName);

    String getDbName();

    boolean addTable(String tableName);

    void warmUp();

}
