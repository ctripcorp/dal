package com.ctrip.platform.dal.sqlserverjdbc;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "UnitTest_SqlServer")
@Table(name = "TestNvarchar")
public class SqlServerJDBCPojo {
    @Column(name = "ID")
    @Type(value = Types.INTEGER)
    private Integer ID;

    @Column(name = "nvarcharField")
    @Type(value = Types.NVARCHAR)
    private String nvarcharField;

    @Column(name = "varcharField")
    @Type(value = Types.VARCHAR)
    private String varcharField;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getNvarcharField() {
        return nvarcharField;
    }

    public void setNvarcharField(String nvarcharField) {
        this.nvarcharField = nvarcharField;
    }

    public String getVarcharField() {
        return varcharField;
    }

    public void setVarcharField(String varcharField) {
        this.varcharField = varcharField;
    }

}
