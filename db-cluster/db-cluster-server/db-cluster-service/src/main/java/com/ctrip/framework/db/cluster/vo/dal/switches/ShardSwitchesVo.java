package com.ctrip.framework.db.cluster.vo.dal.switches;

import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by @author zhuYongMing on 2019/10/28.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShardSwitchesVo {

    private Integer shardIndex;

    private DatabaseSwitchesVo master;

    private DatabaseSwitchesVo slave;

    private DatabaseSwitchesVo read;


    public void valid(final RegexMatcher regexMatcher) {
        // shardIndex
        Preconditions.checkNotNull(shardIndex, "shardIndex不允许为空.");

        // master, slave, read
        if (null == master && null == slave && null == read) {
            throw new IllegalArgumentException("master, slave, read不允许同时为空.");
        }

        // master
        if (null != master) {
            Preconditions.checkArgument(
                    StringUtils.isNotBlank(master.getDomain()),
                    "如果master节点存在, 即表示需要对master节点信息进行切换, 此时要求master domain也必须存在."
            );
            Preconditions.checkArgument(
                    null != master.getInstance(),
                    "如果master节点存在, 即表示需要对master节点信息进行切换, 此时master instance字段必须存在."
            );
            Preconditions.checkArgument(CollectionUtils.isEmpty(master.getInstances()), "master instances字段不能赋值.");
            master.valid(regexMatcher);
        }
        // slave
        if (null != slave) {
            Preconditions.checkArgument(null == slave.getInstance(), "slave instance字段不能赋值.");
            slave.valid(regexMatcher);
        }
        // read
        if (null != read) {
            Preconditions.checkArgument(null == read.getInstance(), "read instance字段不能赋值.");
            read.valid(regexMatcher);
        }
    }

    public void correct() {
        // master
        if (null != master) {
            master.correct();
        }
        // slave
        if (null != slave) {
            slave.correct();
        }
        // read
        if (null != read) {
            read.correct();
        }
    }
}
