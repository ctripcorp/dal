package com.ctrip.framework.db.cluster.vo.mongo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/3/26.
 */
@Data
@Builder
public class ContactVo {
    private String user;
    private String email;
    private String phone;
}
