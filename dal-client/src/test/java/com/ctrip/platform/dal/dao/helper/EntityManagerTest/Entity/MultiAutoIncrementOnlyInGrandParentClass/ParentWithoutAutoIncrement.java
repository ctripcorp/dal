package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiAutoIncrementOnlyInGrandParentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance.GrandParent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "ParentDatabase")
@Table(name = "ParentTable")
public class ParentWithoutAutoIncrement extends GrandParent {

    @Column(name = "parentName")
    @Type(value = Types.VARCHAR)
    @Sensitive(value = true)
    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
