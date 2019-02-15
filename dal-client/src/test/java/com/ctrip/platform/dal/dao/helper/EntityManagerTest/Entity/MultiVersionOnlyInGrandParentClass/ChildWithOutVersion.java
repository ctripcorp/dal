package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionOnlyInGrandParentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "ChildDatabase")
@Table(name = "ChildTable")
public class ChildWithOutVersion extends ParentWithoutVersion {

    @Column(name = "childName")
    @Type(value = Types.VARCHAR)
    private String childName;


    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }
}
