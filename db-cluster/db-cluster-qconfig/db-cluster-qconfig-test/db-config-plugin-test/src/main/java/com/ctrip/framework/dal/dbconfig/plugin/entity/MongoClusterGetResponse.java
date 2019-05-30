package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/5/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoClusterGetResponse {
    private int status;
    private String message;
    private MongoClusterGetOutputEntity data;
}