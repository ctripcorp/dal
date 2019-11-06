package com.ctrip.framework.db.cluster.domain.plugin.titan.update;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/6/25.
 */
@Data
@Builder
public class TitanKeyUpdateDBData {
    private String dbName;
    private String domain;
    private String ip;
    private Integer port;
}
