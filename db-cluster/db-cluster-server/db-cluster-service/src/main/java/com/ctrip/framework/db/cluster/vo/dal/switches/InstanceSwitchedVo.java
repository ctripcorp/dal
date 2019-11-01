package com.ctrip.framework.db.cluster.vo.dal.switches;

import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.google.common.base.Preconditions;
import lombok.*;
import org.apache.commons.lang.StringUtils;

/**
 * Created by @author zhuYongMing on 2019/10/28.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"ip", "port"})
public class InstanceSwitchedVo {

    private String ip;

    private Integer port;

    private Integer readWeight;

    private String tags;


    public void valid(final RegexMatcher regexMatcher) {
        // ip
        Preconditions.checkArgument(StringUtils.isNotBlank(this.ip), "instance ip不允许为空.");
        Preconditions.checkArgument(regexMatcher.ip(this.ip), "instance ip不合法.");

        // port
        Preconditions.checkNotNull(this.port, "instance port不允许为空.");
        Preconditions.checkArgument(regexMatcher.port(this.port.toString()), "instance port不合法.");
    }

    public void correct() {
        // readWeight
        if (null == readWeight) {
            readWeight = 1;
        }

        // tags
        if (StringUtils.isBlank(tags)) {
            tags = "";
        }
    }
}
