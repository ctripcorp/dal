package com.ctrip.platform.dal.application.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

/**
     * @author sly石李颖
     * @date 2020-10-23
     */
    @Entity
    @Database(name = "bbzbbzdrcbenchmarktmpdb_dalcluster")
    @Table(name = "order_config")
    public class OrderConfig implements DalPojo {

        /**
         * 空
         */
        @Id
        @Column(name = "ID")
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Type(value = Types.INTEGER)
        private Integer iD;

        /**
         * 业务类型（OrderDbType）
         */
        @Column(name = "BizType")
        @Type(value = Types.SMALLINT)
        private Integer bizType;

        /**
         * 订单
         */
        @Column(name = "OrderType")
        @Type(value = Types.SMALLINT)
        private Integer orderType;

        /**
         * 配置键
         */
        @Column(name = "ConfigKey")
        @Type(value = Types.VARCHAR)
        private String configKey;

        /**
         * 配置值
         */
        @Column(name = "ConfigValue")
        @Type(value = Types.VARCHAR)
        private String configValue;

        /**
         * 描述
         */
        @Column(name = "Description")
        @Type(value = Types.VARCHAR)
        private String description;

        /**
         * 配置状态
         */
        @Column(name = "Status")
        @Type(value = Types.INTEGER)
        private Integer status;

        /**
         * 时间戳（精确到毫秒）
         */
        @Column(name = "DataChange_LastTime", insertable = false, updatable = false)
        @Type(value = Types.TIMESTAMP)
        private Timestamp datachangeLasttime;

        public Integer getID() {
            return iD;
        }

        public void setID(Integer iD) {
            this.iD = iD;
        }

        public Integer getBizType() {
            return bizType;
        }

        public void setBizType(Integer bizType) {
            this.bizType = bizType;
        }

        public Integer getOrderType() {
            return orderType;
        }

        public void setOrderType(Integer orderType) {
            this.orderType = orderType;
        }

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Timestamp getDatachangeLasttime() {
            return datachangeLasttime;
        }

        public void setDatachangeLasttime(Timestamp datachangeLasttime) {
            this.datachangeLasttime = datachangeLasttime;
        }

    }