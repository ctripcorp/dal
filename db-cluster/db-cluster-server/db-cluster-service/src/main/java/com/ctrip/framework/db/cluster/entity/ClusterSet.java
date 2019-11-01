package com.ctrip.framework.db.cluster.entity;

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
 * Created by @author zhuYongMing on 2019/10/23.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Database(name = "dalclusterdemodb_w")
@Table(name = "cluster_set")
public class ClusterSet {

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
     * set_id
     */
    @Column(name = "set_id")
    @Type(value = Types.VARCHAR)
    private String setId;

    /**
     * 所属区域region
     */
    @Column(name = "region")
    @Type(value = Types.VARCHAR)
    private String region;

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
