package com.ctrip.framework.db.cluster.domain.plugin.titan.add;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/3/18.
 */
@Data
@Builder
public class TitanKeyInfo {
    private String keyName;
    private String providerName;
    private String serverName;
    private String serverIp;
    private String port;
    private String uid;
    private String password;
    private String dbName;
    private String extParam;
    private int timeOut;
    private boolean enabled;
    private String createUser;
    private String updateUser;
    private String whiteList;
    private String blackList;
    private String permissions;
    private String freeVerifyIpList;
    private String freeVerifyAppIdList;
}
