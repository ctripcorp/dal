package com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.sql.Types;

@Entity
@Database(name = "GrandParentDatabase")
@Table(name = "GrandParentTable")
public class GrandParent implements DalPojo {
    @Id
    @Column(name = "grandParentId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private int grandParentId;

    @Column(name = "grandParentName")
    @Type(value = Types.VARCHAR)
    @Sensitive(value = true)
    @Version
    private String grandParentName;

    public int getGrandParentId() {
        return grandParentId;
    }

    public void setGrandParentId(int grandParentId) {
        this.grandParentId = grandParentId;
    }

    public String getGrandParentName() {
        return grandParentName;
    }

    public void setGrandParentName(String grandParentName) {
        this.grandParentName = grandParentName;
    }
}
