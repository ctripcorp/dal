package com.ctrip.framework.db.cluster.vo.dal.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by @author zhuYongMing on 2019/11/5.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterExtensionConfigVo {

    private String content;

    private String typeName;
}
