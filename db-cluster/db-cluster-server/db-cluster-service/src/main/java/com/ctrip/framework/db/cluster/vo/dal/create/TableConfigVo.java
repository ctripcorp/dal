package com.ctrip.framework.db.cluster.vo.dal.create;

import com.ctrip.framework.db.cluster.util.Utils;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

/**
 * Created by @author zhuYongMing on 2019/11/26.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableConfigVo {

    private String tableName;

    private String unitShardColumn;


    public void valid() {
        Preconditions.checkArgument(StringUtils.isNotBlank(tableName), "table name can't be blank.");
        Preconditions.checkArgument(StringUtils.isNotBlank(unitShardColumn), "unit shard column can't be blank.");
    }

    public void correct() {
        tableName = Utils.format(tableName);
        unitShardColumn = Utils.format(unitShardColumn);
    }
}
