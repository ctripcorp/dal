package com.ctrip.framework.db.cluster.domain.plugin.titan.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/4/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitanKeyGetResponse {

    private int status;

    private String message;

    private TitanKeyGetData data;
}
