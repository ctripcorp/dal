package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "ParentDatabase")
@Table(name = "ParentTable")
public class Parent extends GrandParent {
    @Id
    @Column(name = "parentId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private int parentId;

    @Column(name = "parentName")
    @Type(value = Types.VARCHAR)
    @Sensitive(value = true)
    private String parentName;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
