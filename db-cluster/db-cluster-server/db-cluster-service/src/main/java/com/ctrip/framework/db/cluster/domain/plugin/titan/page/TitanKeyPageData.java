package com.ctrip.framework.db.cluster.domain.plugin.titan.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 * format see http://qconfig.ctripcorp.com/plugins/titan/configs?appid=100010061&env=fat&pageNo=1&pageSize=3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitanKeyPageData {

    private List<TitanKeyPageSingleData> data;

    private Integer totalPage;

    private Integer total;

    private Integer pageSize;

    private Integer page;
}
