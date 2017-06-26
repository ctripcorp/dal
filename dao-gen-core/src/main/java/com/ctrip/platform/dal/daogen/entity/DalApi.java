package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.DalPojo;
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
@Database(name = "dao")
@Table(name = "api_list")
public class DalApi implements Comparable<DalApi>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "language")
    @Type(value = Types.VARCHAR)
    private String language;

    @Column(name = "db_type")
    @Type(value = Types.VARCHAR)
    private String dbType;

    @Column(name = "crud_type")
    @Type(value = Types.VARCHAR)
    private String crudType;

    @Column(name = "method_declaration")
    @Type(value = Types.VARCHAR)
    private String methodDeclaration;

    @Column(name = "method_description")
    @Type(value = Types.LONGVARCHAR)
    private String methodDescription;

    @Column(name = "sp_type")
    @Type(value = Types.VARCHAR)
    private String spType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getCrudType() {
        return crudType;
    }

    public void setCrudType(String crudType) {
        this.crudType = crudType;
    }

    public String getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(String methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public String getMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public String getSpType() {
        return spType;
    }

    public void setSpType(String spType) {
        this.spType = spType;
    }

    @Override
    public int compareTo(DalApi api) {
        String str1 = language + dbType + crudType + methodDeclaration + methodDescription + spType;
        String str2 = api.getLanguage() + api.getDbType() + api.getCrudType() + api.getMethodDeclaration()
                + api.getMethodDescription() + api.getSpType();
        return str1.compareTo(str2);
    }
}
