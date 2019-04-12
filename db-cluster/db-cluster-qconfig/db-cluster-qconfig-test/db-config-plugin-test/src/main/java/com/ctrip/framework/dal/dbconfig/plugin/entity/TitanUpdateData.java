package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/4/9.
 */
@Data
@Builder
public class TitanUpdateData {
    @SerializedName("keyname")
    private String keyName;

    private String server;
    private Integer port;
}
