package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/4/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SslCodeGetResponse {
    private int status;
    private String message;
    private String data;
}
