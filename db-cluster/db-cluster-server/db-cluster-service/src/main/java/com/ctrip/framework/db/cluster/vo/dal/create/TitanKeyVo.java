package com.ctrip.framework.db.cluster.vo.dal.create;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/3/7.
 */
@Data
@Builder
public class TitanKeyVo {
    private String keyName;
    private String uid;
    private String extParam;
    private Integer timeOut;
    private Boolean enabled;
    private String createUser;
    private String updateUser;
    private String permissions;
    private String freeVerifyIpList;
    private String freeVerifyAppIdList;
}
