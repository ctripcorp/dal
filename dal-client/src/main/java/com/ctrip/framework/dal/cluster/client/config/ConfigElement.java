package com.ctrip.framework.dal.cluster.client.config;


import com.ctrip.framework.dal.cluster.client.base.Lifecycle;

/**
 * @author c7ch23en
 */
public interface ConfigElement extends Lifecycle {

    void addSubElement(ConfigElement subElement);

}
