package com.ctrip.framework.db.cluster.domain.plugin.titan.switches;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/6/25.
 */
@Data
@Builder
public class MhaUpdateData {
    @SerializedName("keyname")
    private String keyName;
    private String server;
    private Integer port;
}
