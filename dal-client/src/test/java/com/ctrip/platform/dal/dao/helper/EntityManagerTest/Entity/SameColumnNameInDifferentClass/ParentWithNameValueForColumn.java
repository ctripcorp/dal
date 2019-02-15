package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.SameColumnNameInDifferentClass;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "ParentDatabase")
@Table(name = "ParentTable")
public class ParentWithNameValueForColumn extends GrandParentWithNameValueForColumn {
    @Column(name = "Name")
    @Type(value = Types.VARCHAR)
    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
