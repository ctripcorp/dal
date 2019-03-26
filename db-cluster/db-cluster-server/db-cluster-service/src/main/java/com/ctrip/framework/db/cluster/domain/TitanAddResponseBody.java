package com.ctrip.framework.db.cluster.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/3/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitanAddResponseBody {
    private int status;
    private String message;
    private Object data;
}
