package com.ctrip.framework.dal.dbconfig.plugin.entity.mongo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/4/3.
 */
@Data
@Builder
public class Node {
    private String host;
    private Integer port;
}
