package com.ctrip.framework.db.cluster.domain.plugin.titan.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TitanKeyPageSingleConnectionData {

    private String server;

    private String serverIp;

    private Integer port;

    private String uid;

    private String password;

    private String dbName;

    private String extParam;
}
