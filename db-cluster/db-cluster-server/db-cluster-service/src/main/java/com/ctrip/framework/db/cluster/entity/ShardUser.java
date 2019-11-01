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
@Table(name = "shard_user")
public class ShardUser implements DalPojo {

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
     * uid
     */
    @Column(name = "uid")
    @Type(value = Types.VARCHAR)
    private String uid;

    /**
     * 密码
     */
    @Column(name = "password")
    @Type(value = Types.VARCHAR)
    private String password;

    /**
     * 操作类型
     */
    @Column(name = "operation_type")
    @Type(value = Types.VARCHAR)
    private String operationType;

    /**
     * 标签
     */
    @Column(name = "tag")
    @Type(value = Types.VARCHAR)
    private String tag;

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
