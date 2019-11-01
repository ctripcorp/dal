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
@Table(name = "shard_instance")
public class ShardInstance implements DalPojo {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    /**
     * shard_id
     */
    @Column(name = "shard_id")
    @Type(value = Types.INTEGER)
    private Integer shardId;

    /**
     * instance_id
     */
    @Column(name = "instance_id")
    @Type(value = Types.INTEGER)
    private Integer instanceId;

    /**
     * 角色
     */
    @Column(name = "role")
    @Type(value = Types.VARCHAR)
    private String role;

    /**
     * 读权重
     */
    @Column(name = "read_weight")
    @Type(value = Types.TINYINT)
    private Integer readWeight;

    /**
     * 标签
     */
    @Column(name = "tags")
    @Type(value = Types.VARCHAR)
    private String tags;

    /**
     * 人为控制是否可用
     */
    @Column(name = "member_status")
    @Type(value = Types.TINYINT)
    private Integer memberStatus;

    /**
     * 健康检测结果控制是否可用
     */
    @Column(name = "health_status")
    @Type(value = Types.TINYINT)
    private Integer healthStatus;

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
