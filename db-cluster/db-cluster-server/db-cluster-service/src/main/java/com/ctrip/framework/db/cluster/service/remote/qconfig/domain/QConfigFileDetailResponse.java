package com.ctrip.framework.db.cluster.service.remote.qconfig.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by @author zhuYongMing on 2019/11/11.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QConfigFileDetailResponse {

    private Integer status;

    private String message;

    private QConfigFileDetailData data;


    public boolean isLegal() {
        return status == 0 && null != data && data.isLegal();
    }
}
