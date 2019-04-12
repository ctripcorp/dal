package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import qunar.tc.qconfig.common.bean.PaginationResult;

/**
 * Created by shenjie on 2019/4/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitanKeyListResponse {
    private int status;
    private String message;
    private PaginationResult data;
}
