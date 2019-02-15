package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.SameColumnNameInDifferentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "GrandParentDatabase")
@Table(name = "GrandParentTable")
public class GrandParentWithNameValueForColumn {
    @Column(name = "Name")
    @Type(value = Types.VARCHAR)
    private String grandParentName;

    public String getGrandParentName() {
        return grandParentName;
    }

    public void setGrandParentName(String grandParentName) {
        this.grandParentName = grandParentName;
    }
}
