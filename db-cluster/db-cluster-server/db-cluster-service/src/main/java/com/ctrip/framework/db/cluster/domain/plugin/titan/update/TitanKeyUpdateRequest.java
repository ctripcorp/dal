package com.ctrip.framework.db.cluster.domain.plugin.titan.update;

import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.TitanKeyMhaUpdateData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/6/25.
 */
@Data
@Builder
public class TitanKeyUpdateRequest {
    private List<TitanKeyUpdateDBData> dbData;
    private List<TitanKeyMhaUpdateData> mhaData;
    private String env;
}
