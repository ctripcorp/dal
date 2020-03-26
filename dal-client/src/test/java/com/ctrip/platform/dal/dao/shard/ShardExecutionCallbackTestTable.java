package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author c7ch23en
 */
@Database(name = "dao_test_mysql_exception_shard")
@Table(name = "shard_tbl")
public class ShardExecutionCallbackTestTable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "dbIndex")
    @Type(value = Types.INTEGER)
    private Integer dbIndex;

    @Column(name = "tableIndex")
    @Type(value = Types.INTEGER)
    private Integer tableIndex;

    @Column(name = "intCol")
    @Type(value = Types.INTEGER)
    private Integer intCol;

    @Column(name = "charCol")
    @Type(value = Types.VARCHAR)
    private String charCol;

/*
    @Column(name = "lastUpdateTime")
    @Type(value = Types.TIMESTAMP)
    private Timestamp lastUpdateTime;
*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(Integer dbIndex) {
        this.dbIndex = dbIndex;
    }

    public Integer getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(Integer tableIndex) {
        this.tableIndex = tableIndex;
    }

    public Integer getIntCol() {
        return intCol;
    }

    public void setIntCol(Integer intCol) {
        this.intCol = intCol;
    }

    public String getCharCol() {
        return charCol;
    }

    public void setCharCol(String charCol) {
        this.charCol = charCol;
    }

/*
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
*/

}
