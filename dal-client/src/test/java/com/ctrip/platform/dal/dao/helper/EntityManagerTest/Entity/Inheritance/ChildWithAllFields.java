package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "dao_test")
@Table(name = "ChildWithAllFields")
public class ChildWithAllFields extends ParentWithoutId {
    @Column(name = "childId")
    @Type(value = Types.INTEGER)
    private int childId;

    @Column(name = "childName")
    @Type(value = Types.VARCHAR)
    @Sensitive(value = true)
    private String childName;

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }
}

