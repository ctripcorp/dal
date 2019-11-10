package com.ctrip.framework.db.cluster.vo.dal.create;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/6/27.
 */
@Data
@Builder
public class DatabaseGroupVo {
    private List<ShardVo> databases;
}
