package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/3/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginResponse {
    private int status;
    private String message;
    private Object data;
}
