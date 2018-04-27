package com.ctrip.platform.dal.dao.tableDao.insertWithKeyholder.NonAutoIncrementIdentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import java.sql.Types;

import com.ctrip.platform.dal.dao.DalPojo;

@Entity
@Database(name = "dao_test")
@Table(name = "test_non_auto_increment_identity")
public class TestNonAutoIncrementIdentity implements DalPojo {

    @Id
    @Column(name = "id")
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "name")
    @Type(value = Types.VARCHAR)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
