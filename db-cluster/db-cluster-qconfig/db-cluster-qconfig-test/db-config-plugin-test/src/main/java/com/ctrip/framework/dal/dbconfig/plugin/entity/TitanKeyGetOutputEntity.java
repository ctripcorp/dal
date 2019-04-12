package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/4/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitanKeyGetOutputEntity {
    private String subEnv;
    private String keyName;
    private String providerName;
    private String serverName;
    private String serverIp;
    private String port;
    private String uid;
    private String password;
    private String dbName;
    private String extParam;
    private Integer timeOut = 15;
    private String sslCode;
    private Boolean enabled = true;
    private String createUser;
    private String updateUser;
    private String whiteList;
    private String blackList;
    private Integer id;
    private String permissions;
    private String freeVerifyIpList;
    private String freeVerifyAppIdList;
    private String mhaLastUpdateTime;
}
