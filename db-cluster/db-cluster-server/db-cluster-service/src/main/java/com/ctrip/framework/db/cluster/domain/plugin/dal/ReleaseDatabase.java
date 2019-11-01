package com.ctrip.framework.db.cluster.domain.plugin.dal;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/4/18.
 */
@Data
@Builder
public class ReleaseDatabase {

    private String role;

    private String ip;

    private Integer port;

    private String dbName;

    private String uid;

    private String password;

    private Integer readWeight;

    private String tags;
}
