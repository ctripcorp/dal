package com.ctrip.platform.dal.sharding.idgen;

import java.util.Map;

public class IdGeneratorConfig implements IIdGeneratorConfig {

    private IIdGeneratorFactory dbDefaultFactory;
    private Map<String, IIdGeneratorFactory> tableFactoryMap;

    public IdGeneratorConfig(IIdGeneratorFactory dbDefaultFactory) {
        this(dbDefaultFactory, null);
    }
    
    public IdGeneratorConfig(IIdGeneratorFactory dbDefaultFactory,
                             Map<String, IIdGeneratorFactory> tableFactoryMap) {
        this.dbDefaultFactory = dbDefaultFactory;
        this.tableFactoryMap = tableFactoryMap;
    }

    public IdGenerator getIdGenerator(String logicDbName, String tableName) {
        IIdGeneratorFactory factory = getIdGeneratorFactory(tableName);
        if (null == factory) {
            return null;
        }
        return factory.getIdGenerator(getSequenceName(logicDbName, tableName));
    }

    private IIdGeneratorFactory getIdGeneratorFactory(String tableName) {
        if (null == tableName) {
            return null;
        }
        if (null == tableFactoryMap) {
            return dbDefaultFactory;
        }
        tableName = tableName.trim().toLowerCase();
        IIdGeneratorFactory factory = tableFactoryMap.get(tableName);
        if (null == factory) {
            return dbDefaultFactory;
        }
        return factory;
    }

    private String getSequenceName(String logicDbName, String tableName) {
        return (logicDbName + "." + tableName).trim().toLowerCase();
    }

}
