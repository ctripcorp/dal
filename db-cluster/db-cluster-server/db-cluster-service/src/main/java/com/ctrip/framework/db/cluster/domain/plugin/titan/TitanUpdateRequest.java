package com.ctrip.framework.db.cluster.domain.plugin.titan;

import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.MhaUpdateData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/6/25.
 */
@Data
@Builder
public class TitanUpdateRequest {
    private List<TitanUpdateDBData> dbData;
    private List<MhaUpdateData> mhaData;
    private String env;
}
