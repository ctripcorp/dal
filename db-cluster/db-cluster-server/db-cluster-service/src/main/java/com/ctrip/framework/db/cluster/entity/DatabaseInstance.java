package com.ctrip.framework.db.cluster.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Created by shenjie on 2019/4/18.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Database(name = "dalclusterdemodb_w")
@Table(name = "database_instance")
public class DatabaseInstance implements DalPojo {

    // 角色
    @Column(name = "role")
    @Type(value = Types.VARCHAR)
    private String role;

    // ip地址
    @Column(name = "ip")
    @Type(value = Types.VARCHAR)
    private String ip;

    // 端口
    @Column(name = "port")
    @Type(value = Types.INTEGER)
    private Integer port;

    // 所属机房
    @Column(name = "idc")
    @Type(value = Types.VARCHAR)
    private String idc;

    // 状态
    @Column(name = "status")
    @Type(value = Types.INTEGER)
    private Integer status;

    // db名称
    @Column(name = "db_name")
    @Type(value = Types.VARCHAR)
    private String dbName;

    // 读权重
    @Column(name = "read_weight")
    @Type(value = Types.INTEGER)
    private Integer readWeight;

    // 标签
    @Column(name = "tags")
    @Type(value = Types.VARCHAR)
    private String tags;

    // 创建时间
    @Column(name = "create_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp createTime;

    // 更新时间
    @Column(name = "DataChange_LastTime", insertable = false, updatable = false)
    @Type(value = Types.TIMESTAMP)
    private Timestamp updateTime;

}