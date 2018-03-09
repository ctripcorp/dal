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
    private Integer config_type;

    @Column(name = "lang_type")
    @Type(value = Types.INTEGER)
    private Integer lang_type;

    @Column(name = "template")
    @Type(value = Types.VARCHAR)
    private String template;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getConfig_type() {
        return config_type;
    }

    public void setConfig_type(Integer config_type) {
        this.config_type = config_type;
    }

    public Integer getLang_type() {
        return lang_type;
    }

    public void setLang_type(Integer lang_type) {
        this.lang_type = lang_type;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
