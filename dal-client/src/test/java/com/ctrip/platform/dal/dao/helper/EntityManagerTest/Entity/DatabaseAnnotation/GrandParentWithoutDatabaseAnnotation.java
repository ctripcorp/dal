package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.DatabaseAnnotation;

import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import java.sql.Types;

@Entity
public class GrandParentWithoutDatabaseAnnotation {
    @Column(name = "grandParentName")
    @Type(value = Types.VARCHAR)
    @Sensitive(value = true)
    @Version
    private String grandParentName;

}
