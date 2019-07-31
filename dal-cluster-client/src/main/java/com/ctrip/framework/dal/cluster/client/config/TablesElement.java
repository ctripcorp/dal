package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.base.PropertyAccessor;
import com.ctrip.framework.dal.cluster.client.base.PropertyAccessorSupport;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class TablesElement extends PropertyAccessorSupport implements ConfigElement {

    private Map<String, TableElement> tableElements = new HashMap<>();

    public TablesElement() {}

    PropertyAccessor getTableProperties(String tableName) {
        return tableElements.get(tableName);
    }

    Set<String> getTableNames() {
        return tableElements.keySet();
    }

    @Override
    public void addSubElement(ConfigElement subElement) {
        if (subElement instanceof TableElement) {
            TableElement table = (TableElement) subElement;
            if (tableElements.put(table.getName(), table) != null)
                throw new ClusterRuntimeException("duplicate table names defined under <Tables>");
        } else {
            throw new UnsupportedOperationException("<Tables> supports only sub element of <Table>");
        }
    }

    @Override
    public void start() {
        for (TableElement tableElement : tableElements.values()) {
            tableElement.merge(this);
            tableElement.start();
        }
    }

    @Override
    public void stop() {}

}
