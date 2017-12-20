package com.dal.mysql.transaction.shard;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Created by lilj on 2017/7/24.
 */
    @Entity
    @Database(name="ShardColModShardByDBTableOnMysql")
    @Table(name="people")
    public class TransactionWithShardOnMysql implements DalPojo {

        @Id
        @Column(name="ID")
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Type(value=Types.INTEGER)
        private Integer iD;

        @Column(name="CityID")
        @Type(value=Types.INTEGER)
        private Integer cityID;

        @Column(name="Age")
        @Type(value=Types.INTEGER)
        private Integer age;

        @Column(name="Name")
        @Type(value=Types.VARCHAR)
        private String name;

        @Column(name="Birth")
        @Type(value=Types.TIMESTAMP)
        private Timestamp birth;

        public Integer getID() {
            return iD;
        }

        public void setID(Integer iD) {
            this.iD = iD;
        }

        public Integer getCityID() {
            return cityID;
        }

        public void setCityID(Integer cityID) {
            this.cityID = cityID;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Timestamp getBirth() {
            return birth;
        }

        public void setBirth(Timestamp birth) {
            this.birth = birth;
        }

    }


