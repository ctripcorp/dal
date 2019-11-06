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
public class PluginResponse {

    private int status;

    private String message;

    private Object data;

    public static PluginResponse successPluginResponse() {
        return new PluginResponse(PluginStatusCode.OK, null, null);
    }

    public static PluginResponse failPluginResponse(int status, String message) {
        return new PluginResponse(status, message, null);
    }

    public boolean isSuccess() {
        return 0 == status;
    }
}
