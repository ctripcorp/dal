package com.ctrip.framework.db.cluster.domain.plugin.titan.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TitanKeyPageSingleData {

    private Integer id; // ignore

    private String name;

    private String subEnv;

    private Boolean enabled;

    private String connectionString; // ignore

    private String sslCode; // ignore

    private String providerName;

    private Integer timeOut; // ignore

    private String createUser;

    private String updateUser;

    private String whiteList; // ignore

    private String blackList; // ignore

    private String permissions;

    private String freeVerifyIpList;

    private String freeVerifyAppIdList;

    private Timestamp mhaLastUpdateTime;

    private TitanKeyPageSingleConnectionData connectionInfo;


    public boolean isLegal() {
        return StringUtils.isNoneBlank(name) && null != connectionInfo;
    }
}
