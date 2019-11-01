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
 * Created by shenjie on 2019/3/5.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Database(name = "dalclusterdemodb_w")
@Table(name = "cluster_info")
public class Cluster implements DalPojo {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    /**
     * cluster名称
     */
    @Column(name = "cluster_name")
    @Type(value = Types.VARCHAR)
    private String clusterName;

    /**
     * 数据库类型
     */
    @Column(name = "db_category")
    @Type(value = Types.VARCHAR)
    private String dbCategory;

    /**
     * 是否可用
     */
    @Column(name = "enabled")
    @Type(value = Types.TINYINT)
    private Integer enabled;

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
     * 发布时间
     */
    @Column(name = "release_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp releaseTime;

    /**
     * 版本号
     */
    @Column(name = "release_version")
    @Type(value = Types.INTEGER)
    private Integer releaseVersion;

    /**
     * 更新时间
     */
    @Column(name = "datachange_lasttime", insertable = false, updatable = false)
    @Type(value = Types.TIMESTAMP)
    private Timestamp updateTime;

}

