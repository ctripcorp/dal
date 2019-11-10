package com.ctrip.framework.db.cluster.domain.plugin.titan.switches;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/6/25.
 */
@Data
@Builder
public class TitanKeyMhaUpdateRequest {

    private List<TitanKeyMhaUpdateData> data;

    private String env;
}
