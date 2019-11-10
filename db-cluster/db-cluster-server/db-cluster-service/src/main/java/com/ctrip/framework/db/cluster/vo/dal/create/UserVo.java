package com.ctrip.framework.db.cluster.vo.dal.create;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/3/7.
 */
@Data
@Builder
public class UserVo {
    private String username;
    private String password;
    private String permission;
    private String tag;
    private Boolean enabled;
    private String titanKey;
}
