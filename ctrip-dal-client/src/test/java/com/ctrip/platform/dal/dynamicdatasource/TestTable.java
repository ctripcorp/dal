package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "DalService2DB_W")
@Table(name = "test_table")
public class TestTable {

    @Column(name = "Column 1")
    @Type(value = Types.INTEGER)
    private Integer Column1;

    @Column(name = "Column 2")
    @Type(value = Types.INTEGER)
    private Integer Column2;

    public Integer getColumn1() {
        return Column1;
    }

    public void setColumn1(Integer column1) {
        Column1 = column1;
    }

    public Integer getColumn2() {
        return Column2;
    }

    public void setColumn2(Integer column2) {
        Column2 = column2;
    }



}
