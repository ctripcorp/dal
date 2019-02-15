package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionInDifferentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import java.sql.Types;

@Entity
@Database(name = "GrandParentDatabase")
@Table(name = "GrandParentTable")
public class GrandParentWithVersion {
    @Column(name = "grandParentName")
    @Type(value = Types.VARCHAR)
    @Version
    private String grandParentName;

    public String getGrandParentName() {
        return grandParentName;
    }

    public void setGrandParentName(String grandParentName) {
        this.grandParentName = grandParentName;
    }
}
