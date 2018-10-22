package com.ctrip.platform.dal.sharding.idgen;

import java.util.Map;

public class IdGeneratorConfig implements IIdGeneratorConfig {

    private String sequenceDbName;
    private IIdGeneratorFactory dbDefaultFactory;
    private Map<String, IIdGeneratorFactory> tableFactoryMap;

    public IdGeneratorConfig(String sequenceDbName, IIdGeneratorFactory dbDefaultFactory) {
        this(sequenceDbName, dbDefaultFactory, null);
    }
    
    public IdGeneratorConfig(String sequenceDbName, IIdGeneratorFactory dbDefaultFactory,
                             Map<String, IIdGeneratorFactory> tableFactoryMap) {
        this.sequenceDbName = sequenceDbName;
        this.dbDefaultFactory = dbDefaultFactory;
        this.tableFactoryMap = tableFactoryMap;
    }

    @Override
    public IdGenerator getIdGenerator(String tableName) {
        IIdGeneratorFactory factory = getIdGeneratorFactory(tableName);
        if (null == factory) {
            return null;
        }
        return factory.getIdGenerator(getSequenceName(tableName));
    }

    private IIdGeneratorFactory getIdGeneratorFactory(String tableName) {
        if (null == tableName) {
            return null;
        }
        if (null == tableFactoryMap) {
            return dbDefaultFactory;
        }
        IIdGeneratorFactory factory = tableFactoryMap.get(tableName.trim().toLowerCase());
        if (null == factory) {
            return dbDefaultFactory;
        }
        return factory;
    }

    private String getSequenceName(String tableName) {
        return (sequenceDbName + "." + tableName).trim().toLowerCase();
    }

}
