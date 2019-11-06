package com.ctrip.framework.db.cluster.domain.plugin.titan.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitanKeyPageResponse {

    private int status;

    private String message;

    private TitanKeyPageData data;


    public boolean isSuccess() {
        return 0 == status;
    }
}
