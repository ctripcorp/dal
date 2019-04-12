package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.Builder;
import lombok.Data;

/**
 * Created by lzyan on 2018/10/10.
 */
@Data
@Builder
public class FreeVerifyRequest {
    private String titanKeyList;
    private String freeVerifyIpList;
    private String freeVerifyAppIdList;
}
