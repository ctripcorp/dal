package com.ctrip.platform.dal.sharding.idgen;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.helper.ClassScanFilter;
import com.ctrip.platform.dal.dao.helper.DalClassScanner;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IdGeneratorConfig implements IIdGeneratorConfig {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String TYPE_DAL = "DAL";
    private static final String NAME_NO_ENTITY_FOUND = "NO_ENTITY_FOUND";

    private String sequenceDbName;
    private String entityDbName;
    private String entityPackage;
    private Set<String> sequenceTables = new HashSet<>();
    private IIdGeneratorFactory dbDefaultFactory;
    private Map<String, IIdGeneratorFactory> tableFactoryMap;

    public IdGeneratorConfig(String sequenceDbName, IIdGeneratorFactory dbDefaultFactory) {
        this(sequenceDbName, dbDefaultFactory, null);
    }
    
    public IdGeneratorConfig(String sequenceDbName, IIdGeneratorFactory dbDefaultFactory,
                             Map<String, IIdGeneratorFactory> tableFactoryMap) {
        this(sequenceDbName, null, null, dbDefaultFactory, tableFactoryMap);
    }

    public IdGeneratorConfig(String sequenceDbName, String entityDbName, String entityPackage,
                             IIdGeneratorFactory dbDefaultFactory, Map<String, IIdGeneratorFactory> tableFactoryMap) {
        this.sequenceDbName = sequenceDbName;
        this.entityDbName = entityDbName;
        this.entityPackage = entityPackage;
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

    @Override
    public String getSequenceDbName() {
        return sequenceDbName;
    }

    private String getSequenceName(String tableName) {
        return (sequenceDbName + "." + tableName).trim().toLowerCase();
    }

    @Override
    public int warmUp() {
        scanEntities();
        for (String tableName : sequenceTables) {
            getIdGenerator(tableName).nextId();
        }
        return sequenceTables.size();
    }

    private void scanEntities() {
        if (entityDbName!= null && !entityDbName.isEmpty() &&
                entityPackage != null && !entityPackage.isEmpty()) {
            try {
                List<Class<?>> entities = new DalClassScanner(new ClassScanFilter() {
                    @Override
                    public boolean accept(Class<?> clazz) {
                        return clazz.isAnnotationPresent(Entity.class) &&
                                clazz.isAnnotationPresent(Database.class) &&
                                !clazz.isInterface();
                    }
                }).getClasses(entityPackage, true);
                for (Class<?> entity : entities) {
                    Database database = entity.getAnnotation(Database.class);
                    if (entityDbName.equals(database.name())) {
                        sequenceTables.add(parseEntityTableName(entity));
                    }
                }
                if (sequenceTables.isEmpty()) {
                    String name = String.format("%s::entityDbName=%s,entityPackage=%s",
                            NAME_NO_ENTITY_FOUND, entityDbName, entityPackage);
                    LOGGER.logEvent(TYPE_DAL, name, null);
                }
            } catch (Throwable t) {
                LOGGER.error(String.format("Entity scan exception (entityDbName: %s, entityPackage: %s)",
                        entityDbName, entityPackage), t);
            }
        }
    }

    private String parseEntityTableName(Class<?> entityClazz) {
        Table table = entityClazz.getAnnotation(Table.class);
        if (table != null && !table.name().trim().isEmpty()) {
            return table.name().trim();
        }
        Entity entity = entityClazz.getAnnotation(Entity.class);
        if (entity != null && !entity.name().trim().isEmpty()) {
            return entity.name().trim();
        }
        return entityClazz.getSimpleName();
    }

}
