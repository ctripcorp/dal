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
public class DatabaseSwitchesVo {

    private String domain;

    private Integer port;

    private InstanceSwitchedVo instance;

    private List<InstanceSwitchedVo> instances;


    public void valid(final RegexMatcher regexMatcher) {
        // domain
        if (StringUtils.isNotBlank(domain)) {
            Preconditions.checkArgument(regexMatcher.domain(domain), "domain不合法.");
        }

        // port
        if (null != port) {
            Preconditions.checkArgument(regexMatcher.port(port.toString()), "port不合法.");
        }

        // instance
        if (null != instance) {
            instance.valid(regexMatcher);
        }

        // instances
        if (!CollectionUtils.isEmpty(instances)) {
            Preconditions.checkArgument(
                    instances.stream().distinct().count() == instances.size(),
                    "slave或read下不允许出现多个ip, port相同的节点."
            );
            instances.forEach(instance -> instance.valid(regexMatcher));
        }
    }

    public void correct() {
        // instance
        if (null != instance) {
            instance.correct();
        }

        // instances
        if (!CollectionUtils.isEmpty(instances)) {
            instances.forEach(InstanceSwitchedVo::correct);
        }
    }
}
