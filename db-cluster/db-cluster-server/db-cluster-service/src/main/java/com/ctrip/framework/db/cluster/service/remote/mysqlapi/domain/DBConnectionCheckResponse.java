package com.ctrip.framework.db.cluster.service.remote.mysqlapi.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/5/8.
 */
@Data
@Builder
public class DBConnectionCheckResponse {

    private boolean success;

    private String message;
}