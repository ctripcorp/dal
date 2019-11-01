package com.ctrip.framework.db.cluster.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/5/8.
 */
@Data
@Builder
public class DBConnectionCheckRequest {

    @SerializedName("dbtype")
    private String dbType;    //mysql, sqlserver

    private String env;       //pro, uat, lpt, fat

    private String host;

    private int port;

    private String user;

    private String password;

    @SerializedName("dbname")
    private String dbName;

}
