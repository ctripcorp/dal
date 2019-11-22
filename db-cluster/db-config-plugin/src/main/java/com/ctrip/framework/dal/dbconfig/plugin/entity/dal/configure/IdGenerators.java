package com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
// XML文件中的根标识
@XmlRootElement(name = "IdGenerators")
public class IdGenerators {

    @XmlAnyElement
    private List<Object> generators;

    public List<Object> getGenerators() {
        return generators;
    }

    public void setGenerators(List<Object> generators) {
        this.generators = generators;
    }

    @Override
    public String toString() {
        return "IdGenerators{" +
                "generators='" + generators + '\'' +
                '}';
    }

}
