package com.ctrip.platform.dal.cluster.config;


import com.ctrip.platform.dal.cluster.base.PropertyAccessorSupport;

/**
 * @author c7ch23en
 */
public class TableElement extends PropertyAccessorSupport implements ConfigElement {

    private String name;

    public TableElement(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    @Override
    public void addSubElement(ConfigElement subElement) {
        throw new UnsupportedOperationException("<Table> does not support any sub element");
    }

    @Override
    public void start() {}

    @Override
    public void stop() {}

}
