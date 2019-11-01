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
 * Created by shenjie on 2019/3/22.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Database(name = "dalclusterdemodb_w")
@Table(name = "titan_key")
public class TitanKey {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    /**
     * titan key名称
     */
    @Column(name = "name")
    @Type(value = Types.VARCHAR)
    private String name;

    /**
     * user_id
     */
    @Column(name = "user_id")
    @Type(value = Types.INTEGER)
    private Integer userId;

    /**
     * 其他参数
     */
    @Column(name = "ext_param")
    @Type(value = Types.VARCHAR)
    private String extParam;

    /**
     * 超时时间
     */
    @Column(name = "timeout")
    @Type(value = Types.INTEGER)
    private Integer timeout;

    /**
     * 状态
     */
    @Column(name = "status")
    @Type(value = Types.TINYINT)
    private Integer status;

    /**
     * 创建人
     */
    @Column(name = "create_user")
    @Type(value = Types.VARCHAR)
    private String createUser;

    /**
     * 修改人
     */
    @Column(name = "update_user")
    @Type(value = Types.VARCHAR)
    private String updateUser;

    /**
     * 权限允许的应用
     */
    @Column(name = "permissions")
    @Type(value = Types.VARCHAR)
    private String permissions;

    /**
     * 免校验的ip地址
     */
    @Column(name = "free_verify_ips")
    @Type(value = Types.VARCHAR)
    private String freeVerifyIps;

    /**
     * 免cms关系校验的应用
     */
    @Column(name = "free_verify_apps")
    @Type(value = Types.VARCHAR)
    private String freeVerifyApps;

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
