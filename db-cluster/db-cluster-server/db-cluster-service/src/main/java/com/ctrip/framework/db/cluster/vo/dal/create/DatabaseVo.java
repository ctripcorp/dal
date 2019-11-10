package com.ctrip.framework.db.cluster.vo.dal.create;

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
 * Created by shenjie on 2019/3/11.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseVo {

    private String domain;

    private Integer port;

    private InstanceVo instance;

    private List<InstanceVo> instances;


    public void valid(final RegexMatcher regexMatcher) {
        // domain
        Preconditions.checkArgument(StringUtils.isNotBlank(domain), "domain不能为空.");
        Preconditions.checkArgument(regexMatcher.domain(domain), "domain不合法.");

        // port
        Preconditions.checkNotNull(port, "port不允许为空.");
        Preconditions.checkArgument(regexMatcher.port(port.toString()), "port不合法.");

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
            instances.forEach(instanceVo -> instanceVo.valid(regexMatcher));
        }
    }

    public void correct() {
        if (null != instance) {
            instance.correct();
        }

        if (!CollectionUtils.isEmpty(instances)) {
            instances.forEach(InstanceVo::correct);
        }
    }
}