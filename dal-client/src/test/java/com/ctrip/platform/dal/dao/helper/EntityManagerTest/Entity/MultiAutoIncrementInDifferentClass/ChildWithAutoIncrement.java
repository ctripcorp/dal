package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiAutoIncrementInDifferentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "ChildDatabase")
@Table(name = "ChildTable")
public class ChildWithAutoIncrement extends ParentWithAutoIncrement {
    @Id
    @Column(name = "childId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private int childId;

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

}
