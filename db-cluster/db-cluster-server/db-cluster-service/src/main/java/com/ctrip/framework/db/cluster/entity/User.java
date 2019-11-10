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
@Table(name = "user_info")
public class User implements DalPojo {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    /**
     * cluster_id
     */
    @Column(name = "cluster_id")
    @Type(value = Types.INTEGER)
    private Integer clusterId;

    /**
     * shard_index
     */
    @Column(name = "shard_index")
    @Type(value = Types.INTEGER)
    private Integer shardIndex;

    /**
     * username
     */
    @Column(name = "username")
    @Type(value = Types.VARCHAR)
    private String username;

    /**
     * 密码
     */
    @Column(name = "password")
    @Type(value = Types.VARCHAR)
    private String password;

    /**
     * 操作类型
     */
    @Column(name = "permission")
    @Type(value = Types.VARCHAR)
    private String permission;

    /**
     * 标签
     */
    @Column(name = "tag")
    @Type(value = Types.VARCHAR)
    private String tag;

    /**
     * TitanKey
     */
    @Column(name = "titan_key")
    @Type(value = Types.VARCHAR)
    private String titanKey;

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
     * 更新时间
     */
    @Column(name = "datachange_lasttime", insertable = false, updatable = false)
    @Type(value = Types.TIMESTAMP)
    private Timestamp updateTime;

}
