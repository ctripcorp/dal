package com.ctrip.framework.db.cluster.service.remote.qconfig.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by @author zhuYongMing on 2019/11/7.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QConfigFileNameResponse {

    private Integer status;

    private String message;

    private QConfigFileNameData data;


    public boolean isLegal() {
        return 0 == status;
    }
}
