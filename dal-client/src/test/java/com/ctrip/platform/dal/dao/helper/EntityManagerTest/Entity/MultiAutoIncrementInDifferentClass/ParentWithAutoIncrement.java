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
@Database(name = "ParentDatabase")
@Table(name = "ParentTable")
public class ParentWithAutoIncrement {
    @Id
    @Column(name = "parentId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private int parentId;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

}
