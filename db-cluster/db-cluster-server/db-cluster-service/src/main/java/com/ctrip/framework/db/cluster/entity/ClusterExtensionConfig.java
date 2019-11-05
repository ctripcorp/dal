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
 * Created by @author zhuYongMing on 2019/11/5.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Database(name = "dalclusterdemodb_w")
@Table(name = "cluster_extension_config")
public class ClusterExtensionConfig implements DalPojo {

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
     * 扩展配置内容
     */
    @Column(name = "content")
    @Type(value = Types.VARBINARY)
    private String content;

    /**
     * 扩展配置类型
     */
    @Column(name = "type")
    @Type(value = Types.TINYINT)
    private Integer type;

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
