package com.ctrip.framework.db.cluster.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Created by shenjie on 2019/3/6.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Database(name = "dalclusterdemodb_w")
@Table(name = "shard_info")
public class Shard implements DalPojo {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    /**
     * shard索引
     */
    @Column(name = "shard_index")
    @Type(value = Types.INTEGER)
    private Integer shardIndex;

    /**
     * cluster_id
     */
    @Column(name = "cluster_id")
    @Type(value = Types.INTEGER)
    private Integer clusterId;

    /**
     * set_id
     */
    @Column(name = "set_id")
    @Type(value = Types.VARCHAR)
    private String setId;

    /**
     * 数据库名称
     */
    @Column(name = "db_name")
    @Type(value = Types.VARCHAR)
    private String dbName;

    /**
     * master域名
     */
    @Column(name = "master_domain")
    @Type(value = Types.VARCHAR)
    private String masterDomain;

    /**
     * master端口
     */
    @Column(name = "master_port")
    @Type(value = Types.INTEGER)
    private Integer masterPort;

    /**
     * slave域名
     */
    @Column(name = "slave_domain")
    @Type(value = Types.VARCHAR)
    private String slaveDomain;

    /**
     * slave端口
     */
    @Column(name = "slave_port")
    @Type(value = Types.INTEGER)
    private Integer slavePort;

    /**
     * read域名
     */
    @Column(name = "read_domain")
    @Type(value = Types.VARCHAR)
    private String readDomain;

    /**
     * read端口
     */
    @Column(name = "read_port")
    @Type(value = Types.INTEGER)
    private Integer readPort;

    /**
     * 是否删除
     */
    @Column(name = "deleted")
    @Type(value = Types.TINYINT)
    private Integer deleted;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp createTime;

    /**
     * 更新时间
     */
    @Column(name = "datachange_lasttime", insertable = false, updatable = false)
    @Type(value = Types.TIMESTAMP)
    private Timestamp updateTime;

}