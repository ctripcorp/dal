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
     * 子环境
     */
    @Column(name = "sub_env")
    @Type(value = Types.VARCHAR)
    private String subEnv;

    /**
     * 是否可用
     */
    @Column(name = "enabled")
    @Type(value = Types.TINYINT)
    private Integer enabled;

    /**
     * 驱动名称
     */
    @Column(name = "provider_name")
    @Type(value = Types.VARCHAR)
    private String providerName;


    /**
     * 创建用户
     */
    @Column(name = "create_user")
    @Type(value = Types.VARCHAR)
    private String createUser;

    /**
     * 最后修改用户
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
     * 最后切换时间
     */
    @Column(name = "mha_last_update_time")
    @Type(value = Types.VARCHAR)
    private String mhaLastUpdateTime;

    /**
     * 域名
     */
    @Column(name = "domain")
    @Type(value = Types.VARCHAR)
    private String domain;

    // ip地址
    @Column(name = "ip")
    @Type(value = Types.VARCHAR)
    private String ip;

    /**
     * 端口
     */
    @Column(name = "port")
    @Type(value = Types.INTEGER)
    private Integer port;

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
     * 数据库名称
     */
    @Column(name = "db_name")
    @Type(value = Types.VARCHAR)
    private String dbName;

    /**
     * 其他参数
     */
    @Column(name = "ext_params")
    @Type(value = Types.VARCHAR)
    private String extParams;

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
