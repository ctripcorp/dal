package com.ctrip.framework.db.cluster.domain.plugin.dal;

import com.ctrip.framework.db.cluster.domain.plugin.dal.release.ReleaseCluster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/8/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DalClusterGetResponse {
    private int status;
    private String message;
    private ReleaseCluster data;
}
