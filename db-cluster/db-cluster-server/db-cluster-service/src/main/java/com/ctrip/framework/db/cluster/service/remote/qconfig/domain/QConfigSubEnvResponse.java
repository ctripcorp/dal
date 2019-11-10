package com.ctrip.framework.db.cluster.service.remote.qconfig.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by @author zhuYongMing on 2019/11/7.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QConfigSubEnvResponse {

    private Integer status;

    private String message;

    private List<String> data;


    public boolean isLegal() {
        return 0 == status && !CollectionUtils.isEmpty(data);
    }
}
