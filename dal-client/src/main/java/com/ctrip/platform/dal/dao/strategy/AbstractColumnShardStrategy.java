package com.ctrip.platform.dal.dao.strategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

/**
 * This is the base class for similar strategy that uses column/columns to calculate
 * shard. The implementation is case insensitive for both column names and table names 
 * to adapt to most situations.  
 *  
 * @author jhhe
 *
 */
public abstract class AbstractColumnShardStrategy extends AbstractRWSeparationStrategy implements DalShardingStrategy {
    /**
     * Key used to declared columns for locating DB shard.
     */
    public static final String COLUMNS = "columns";
    private static final String COLUMNS_CSHARP = "column";

    /**
     * Key used to declared tables that qualified for table shard. That's not every table is sharded
     */
    public static final String SHARDED_TABLES = "shardedTables";
    
    /**
     * Key used to declared columns for locating table shard.
     */
    public static final String TABLE_COLUMNS = "tableColumns";
    private static final String TABLE_COLUMNS_CSHARP = "tableColumn";
    
    public static final String SEPARATOR = "separator";

    private String[] columnNames;
    private Set<String> shardedTables = new HashSet<>();
    private String[] tableColumnNames;
    private String separator;
    
    /**
     * columns are separated by ','
     * @Override
     */
    public void initialize(Map<String, String> settings) {
        if(settings.containsKey(COLUMNS)) {
            columnNames = parseNames(settings.get(COLUMNS));
        }else {
            if(settings.containsKey(COLUMNS_CSHARP)) {
                columnNames = parseNames(settings.get(COLUMNS_CSHARP));
            }
        }
        
        if(settings.containsKey(SHARDED_TABLES)) {
            String[] tables = parseNames(settings.get(SHARDED_TABLES));
            for(String table: tables)
                shardedTables.add(table);
        }
        
        if(settings.containsKey(TABLE_COLUMNS)) {
            tableColumnNames = parseNames(settings.get(TABLE_COLUMNS));
        }else {
            if(settings.containsKey(TABLE_COLUMNS_CSHARP)) {
                tableColumnNames = parseNames(settings.get(TABLE_COLUMNS_CSHARP));
            }
        }
        
        if(settings.containsKey(SEPARATOR)) {
            separator = settings.get(SEPARATOR);
        }
    }
    
    private String[] parseNames(String value) {
        String[] names = value.split(",");
        for(int i = 0; i < names.length; i++)
            names[i] = names[i].toLowerCase().trim();
        return names;
    }

    @Override
    public boolean isShardingByDb() {
        return columnNames != null;
    }
    
    /**
     * Locate DB shard value
     * @param value column or parameter value
     * @return DB shard id
     */
    abstract public String calculateDbShard(Object value);
    
    /**
     * Locate table shard value
     * @param tableName the rawTableName table name template without any sharding separator or shard id suffix 
     * @param value column or parameter value
     * @return table shard id
     */
    abstract public String calculateTableShard(String rawTableName, Object value);

    public String locateDbShard(DalConfigure configure, String logicDbName,
            DalHints hints) {
        if(!isShardingByDb())
            throw new RuntimeException(String.format("Logic Db %s is not configured to be shard by database", logicDbName));
        
        String shard = hints.getShardId();
        if(shard != null)
            return shard;
        
        // Shard value take the highest priority
        if(hints.is(DalHintEnum.shardValue)) {
            return calculateDbShard(hints.get(DalHintEnum.shardValue));
        }
        
        shard = evaluateDbShard(columnNames, (Map<String, ?>)hints.get(DalHintEnum.shardColValues));
        if(shard != null)
            return shard;
        
        shard = evaluateDbShard(columnNames, (StatementParameters)hints.get(DalHintEnum.parameters));
        if(shard != null)
            return shard;
        
        shard = evaluateDbShard(columnNames, (Map<String, ?>)hints.get(DalHintEnum.fields));
        if(shard != null)
            return shard;
        
        return null;
    }

    @Override
    public boolean isShardingByTable() {
        return tableColumnNames != null;
    }

    @Override
    public String locateTableShard(DalConfigure configure, String logicDbName, String tableName,
            DalHints hints) {
        if(!isShardingByTable())
            throw new RuntimeException(String.format("Logic Db %s is not configured to be shard by table", logicDbName));
        
        String shard = hints.getTableShardId();
        if(shard != null)
            return shard;
        
        // Shard value take the highest priority
        if(hints.is(DalHintEnum.tableShardValue)) {
            return calculateTableShard(tableName, hints.get(DalHintEnum.tableShardValue));
        }
        
        shard = evaluateTableShard(tableName, tableColumnNames, (Map<String, ?>)hints.get(DalHintEnum.shardColValues));
        if(shard != null)
            return shard;
        
        shard = evaluateTableShard(tableName, tableColumnNames, (StatementParameters)hints.get(DalHintEnum.parameters));
        if(shard != null)
            return shard;
        
        shard = evaluateTableShard(tableName, tableColumnNames, (Map<String, ?>)hints.get(DalHintEnum.fields));
        if(shard != null)
            return shard;
        
        return null;
    }
    
    private String evaluateDbShard(String[] columns, StatementParameters parameters) {
        Object value = findValue(columns, parameters);
        return value == null ? null : calculateDbShard(value);
    }
    
    private String evaluateTableShard(String tableName, String[] columns, StatementParameters parameters) {
        Object value = findValue(columns, parameters);
        return value == null ? null : calculateTableShard(tableName, value);
    }
    
    private Object findValue(String[] columns, StatementParameters parameters) {
        if(parameters == null)
            return null;

        for(String column: columns) {
            StatementParameter param = parameters.get(column, ParameterDirection.Input);
            if(param == null || param.getValue() == null)
                continue;
            return param.getValue();
        }

        return null;
    }
    
    private String evaluateDbShard(String[] columns, Map<String, ?> shardColValues) {
        Object value = findValue(columns, shardColValues);
        return value == null ? null : calculateDbShard(value);
    }
    
    private String evaluateTableShard(String tableName, String[] columns, Map<String, ?> shardColValues) {
        Object value = findValue(columns, shardColValues);
        return value == null ? null : calculateTableShard(tableName, value);
    }
    
    private Object findValue(String[] columns, Map<String, ?> colValues) {
        if(colValues == null)
            return null;            
        
        Object value = null;
        
        for(String column: columns) {
            value = colValues.get(column);
            if(value != null)
                return value;

            //To check in case insensitive way
            for(Map.Entry<String, ?> colEntry: colValues.entrySet())
                if(colEntry.getKey().equalsIgnoreCase(column)) {
                    value = colEntry.getValue();
                    if(value != null)
                        return value;
                }
        }
        
        return value;            
    }
    
    @Override
    public boolean isShardingEnable(String tableName) {
        return shardedTables.contains(tableName.toLowerCase());
    }

    @Override
    public String getTableShardSeparator() {
        return separator;
    }
}