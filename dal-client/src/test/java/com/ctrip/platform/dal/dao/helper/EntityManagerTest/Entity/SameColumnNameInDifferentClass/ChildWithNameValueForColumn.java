package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.SameColumnNameInDifferentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "ChildDatabase")
@Table(name = "ChildTable")
public class ChildWithNameValueForColumn extends ParentWithNameValueForColumn {
    @Column(name = "Name")
    @Type(value = Types.VARCHAR)
    private String childName;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

}
