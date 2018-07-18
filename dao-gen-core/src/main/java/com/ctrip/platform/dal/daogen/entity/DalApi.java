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
    private String db_type;

    @Column(name = "crud_type")
    @Type(value = Types.VARCHAR)
    private String crud_type;

    @Column(name = "method_declaration")
    @Type(value = Types.VARCHAR)
    private String method_declaration;

    @Column(name = "method_description")
    @Type(value = Types.LONGVARCHAR)
    private String method_description;

    @Column(name = "sp_type")
    @Type(value = Types.VARCHAR)
    private String sp_type;

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

    public String getDb_type() {
        return db_type;
    }

    public void setDb_type(String db_type) {
        this.db_type = db_type;
    }

    public String getCrud_type() {
        return crud_type;
    }

    public void setCrud_type(String crud_type) {
        this.crud_type = crud_type;
    }

    public String getMethod_declaration() {
        return method_declaration;
    }

    public void setMethod_declaration(String method_declaration) {
        this.method_declaration = method_declaration;
    }

    public String getMethod_description() {
        return method_description;
    }

    public void setMethod_description(String method_description) {
        this.method_description = method_description;
    }

    public String getSp_type() {
        return sp_type;
    }

    public void setSp_type(String sp_type) {
        this.sp_type = sp_type;
    }

    @Override
    public int compareTo(DalApi api) {
        String str1 = language + db_type + crud_type + method_declaration + method_description + sp_type;
        String str2 = api.getLanguage() + api.getDb_type() + api.getCrud_type() + api.getMethod_declaration()
                + api.getMethod_description() + api.getSp_type();
        return str1.compareTo(str2);
    }
}
