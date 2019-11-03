package com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by shenjie on 2019/5/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
// XML文件中的根标识
@XmlRootElement(name = "DAL")
public class DalConfigure {

    @XmlElement(name = "Cluster")
    private Cluster cluster;

    public DalConfigure() {
    }

    public DalConfigure(Cluster cluster) {
        this.cluster = cluster;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "DalConfigure{" +
                "cluster=" + cluster +
                '}';
    }
}
