package com.ctrip.framework.db.cluster.domain.plugin.titan;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/6/25.
 */
@Data
@Builder
public class TitanUpdateDBData {
    private String dbName;
    private String domain;
    private String ip;
    private Integer port;
}
