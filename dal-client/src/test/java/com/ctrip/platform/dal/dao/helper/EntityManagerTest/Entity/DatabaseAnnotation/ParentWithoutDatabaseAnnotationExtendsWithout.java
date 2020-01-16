package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.DatabaseAnnotation;

import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
public class ParentWithoutDatabaseAnnotationExtendsWithout extends GrandParentWithoutDatabaseAnnotation {
    @Column(name = "parentName")
    @Type(value = Types.VARCHAR)
    @Sensitive(value = true)
    private String parentName;

}
