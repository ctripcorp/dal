package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/4/9.
 */
@Data
@Builder
public class TitanKeyUpdateRequest {
    private String env;
    private List<TitanUpdateData> data;
}
