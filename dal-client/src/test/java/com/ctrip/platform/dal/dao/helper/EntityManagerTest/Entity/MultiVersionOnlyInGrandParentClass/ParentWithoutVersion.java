package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionOnlyInGrandParentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionInDifferentClass.GrandParentWithVersion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "ParentDatabase")
@Table(name = "ParentTable")
public class ParentWithoutVersion extends GrandParentWithVersion {

    @Column(name = "parentName")
    @Type(value = Types.VARCHAR)
    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

}
