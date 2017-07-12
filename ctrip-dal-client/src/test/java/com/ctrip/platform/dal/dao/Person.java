package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "TVP")
@Table(name = "Person")
public class Person implements DalPojo {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private int id;

    @Column(name = "Name")
    @Type(value = Types.NVARCHAR)
    private String name;

    @Column(name = "Age")
    @Type(value = Types.INTEGER)
    private Integer age;

    @Column(name = "Birth")
    @Type(value = Types.TIMESTAMP)
    private Timestamp birth;

    @Column(name = "Test")
    @Type(value = Types.NVARCHAR)
    private String test;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Timestamp getBirth() {
        return birth;
    }

    public void setBirth(Timestamp birth) {
        this.birth = birth;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

}
