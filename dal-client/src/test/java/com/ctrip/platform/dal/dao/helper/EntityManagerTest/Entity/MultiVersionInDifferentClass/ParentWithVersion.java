package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionInDifferentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import java.sql.Types;

@Entity
@Database(name = "ParentDatabase")
@Table(name = "ParentTable")
public class ParentWithVersion extends GrandParentWithVersion {

    @Column(name = "parentName")
    @Type(value = Types.VARCHAR)
    @Version
    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
