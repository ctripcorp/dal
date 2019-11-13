package com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by shenjie on 2019/5/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
// XML文件中的根标识
@XmlRootElement(name = "DatabaseShards")
public class DatabaseShards {

    @XmlElement(name="DatabaseShard")
    private List<DatabaseShard> databaseShards;

    public DatabaseShards() {
    }

    public DatabaseShards(List<DatabaseShard> databaseShards) {
        this.databaseShards = databaseShards;
    }

    public List<DatabaseShard> getDatabaseShards() {
        return databaseShards;
    }

    public void setDatabaseShards(List<DatabaseShard> databaseShards) {
        this.databaseShards = databaseShards;
    }

    @Override
    public String toString() {
        return "DatabaseShards{" +
                "databaseShards=" + databaseShards +
                '}';
    }
}
