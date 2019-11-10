package com.ctrip.framework.db.cluster.domain.plugin.titan.switches;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/6/25.
 */
@Data
@Builder
@AllArgsConstructor
public class TitanKeyMhaUpdateData {

    @SerializedName("keyname")
    private String keyName;

    private String server;

    private Integer port;
}
