package com.ctrip.framework.db.cluster.domain.plugin.dal.releases;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/4/18.
 */
@Data
@Builder
public class ReleaseShard {

    private Integer index;

    private String masterDomain;

    private String slaveDomain;

    private Integer masterPort;

    private Integer slavePort;

    private String masterTitanKeys;

    private String slaveTitanKeys;

    private List<ReleaseDatabase> databases;
}
