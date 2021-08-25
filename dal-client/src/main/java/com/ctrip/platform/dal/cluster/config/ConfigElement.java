package com.ctrip.platform.dal.cluster.config;


import com.ctrip.platform.dal.cluster.base.Lifecycle;

/**
 * @author c7ch23en
 */
public interface ConfigElement extends Lifecycle {

    void addSubElement(ConfigElement subElement);

}
