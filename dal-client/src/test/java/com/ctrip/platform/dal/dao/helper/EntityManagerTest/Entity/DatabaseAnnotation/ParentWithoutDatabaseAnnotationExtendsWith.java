package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.DatabaseAnnotation;

import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Types;

@Entity
public class ParentWithoutDatabaseAnnotationExtendsWith extends GrandParentWithDatabaseAnnotation {
    @Column(name = "parentName")
    @Type(value = Types.VARCHAR)
    @Sensitive(value = true)
    private String parentName;
}
