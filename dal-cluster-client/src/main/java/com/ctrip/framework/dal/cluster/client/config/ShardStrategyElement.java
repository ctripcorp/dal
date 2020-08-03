package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.base.PropertyAccessor;
import com.ctrip.framework.dal.cluster.client.base.PropertyAccessorSupport;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.sharding.strategy.ShardStrategy;

import java.util.*;

/**
 * @author c7ch23en
 */
public abstract class ShardStrategyElement extends PropertyAccessorSupport implements ConfigElement, ShardStrategy {

    private final List<TablesElement> tablesElements = new LinkedList<>();
    private final Map<String, PropertyAccessor> tableProperties = new HashMap<>();

    public ShardStrategyElement() {}

    @Override
    public Set<String> getAppliedTables() {
        Set<String> tableNames = new HashSet<>();
        for (TablesElement tables : tablesElements)
            tableNames.addAll(tables.getTableNames());
        return tableNames;
    }

    protected String getTableProperty(String tableName, String propertyName) {
        return getTableProperty(tableName, propertyName, null);
    }

    protected String getTableProperty(String tableName, String propertyName, String defaultValue) {
        PropertyAccessor properties = getTableProperties(tableName);
        String property = properties.getProperty(propertyName);
        return property != null ? property : defaultValue;
    }

    protected Boolean getTableBooleanProperty(String tableName, String propertyName) {
        return getTableBooleanProperty(tableName, propertyName, null);
    }

    protected Boolean getTableBooleanProperty(String tableName, String propertyName, Boolean defaultValue) {
        String property = getTableProperty(tableName, propertyName);
        return property != null ? Boolean.parseBoolean(property) : defaultValue;
    }

    protected Integer getTableIntProperty(String tableName, String propertyName) {
        return getTableIntProperty(tableName, propertyName, null);
    }

    protected Integer getTableIntProperty(String tableName, String propertyName, Integer defaultValue) {
        String property = getTableProperty(tableName, propertyName);
        if (property == null)
            return defaultValue;
        try {
            return Integer.parseInt(property);
        } catch (Throwable t) {
            throw new ClusterRuntimeException("illegal int property", t);
        }
    }

    protected Long getTableLongProperty(String tableName, String propertyName) {
        return getTableLongProperty(tableName, propertyName, null);
    }

    protected Long getTableLongProperty(String tableName, String propertyName, Long defaultValue) {
        String property = getTableProperty(tableName, propertyName);
        if (property == null)
            return defaultValue;
        try {
            return Long.parseLong(property);
        } catch (Throwable t) {
            throw new ClusterRuntimeException("illegal long property", t);
        }
    }

    private PropertyAccessor getTableProperties(String tableName) {
        if (tableName == null)
            return this;
        PropertyAccessor properties = tableProperties.get(tableName);
        return properties != null ? properties : this;
    }

    @Override
    public void addSubElement(ConfigElement subElement) {
        if (subElement instanceof TablesElement)
            tablesElements.add((TablesElement) subElement);
        else
            throw new UnsupportedOperationException("ShardStrategyElement supports only sub element of <Tables>");
    }

    @Override
    public void start() {
        for (TablesElement tablesElement : tablesElements) {
            tablesElement.merge(this);
            tablesElement.start();
            for (String tableName : tablesElement.getTableNames()) {
                PropertyAccessor properties = tablesElement.getTableProperties(tableName);
                if (tableProperties.put(tableName, properties) != null)
                    throw new ClusterRuntimeException("duplicate table names defined under ShardStrategyElement");
            }
        }
    }

    @Override
    public void stop() {}

}
