package com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
// XML文件中的根标识
@XmlRootElement(name = "ShardStrategies")
public class ShardStrategies {

    @XmlAnyElement
    private List<Object> strategies;

    public List<Object> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<Object> strategies) {
        this.strategies = strategies;
    }

    @Override
    public String toString() {
        return "ShardStrategies{" +
                "strategies='" + strategies + '\'' +
                '}';
    }

}
