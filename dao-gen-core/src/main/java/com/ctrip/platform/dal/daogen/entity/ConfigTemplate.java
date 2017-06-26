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
@Table(name = "config_template")
public class ConfigTemplate implements DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "config_type")
    @Type(value = Types.INTEGER)
    private Integer configType;

    @Column(name = "lang_type")
    @Type(value = Types.INTEGER)
    private Integer langType;

    @Column(name = "template")
    @Type(value = Types.VARCHAR)
    private String template;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigType(Integer configType) {
        this.configType = configType;
    }

    public Integer getLangType() {
        return langType;
    }

    public void setLangType(Integer langType) {
        this.langType = langType;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
