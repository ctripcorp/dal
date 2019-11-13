package com.ctrip.framework.db.cluster.service.remote.qconfig.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * Created by @author zhuYongMing on 2019/11/11.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QConfigFileDetailData {

    private String data;


    public boolean isLegal() {
        return StringUtils.isNotBlank(data);
    }
}
