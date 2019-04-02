package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.domain.MongoCluster;
import com.ctrip.framework.db.cluster.domain.Node;
import com.ctrip.framework.db.cluster.enums.ClusterType;
import com.ctrip.framework.db.cluster.enums.Env;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


/**
 * Created by shenjie on 2019/3/19.
 */
@Component
public class ValidityChecker {

    @Autowired
    private RegexMatcher regexMatcher;

    public boolean checkAllowedIp(String ip, Set<String> allowedIps) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        if (allowedIps == null) {
            return false;
        }

        return allowedIps.contains(ip);
    }

    public String checkAndGetEnv(String env) {
        if (StringUtils.isBlank(env)) {
            return Env.PRO.name().toLowerCase();
        } else {
            return env.trim().toLowerCase();
        }
    }

    public void checkOperator(String operator) {
        Preconditions.checkArgument(StringUtils.isNotBlank(operator), "Operator为空");
    }

    public void checkMongoCluster(MongoCluster mongoCluster) {
        Preconditions.checkNotNull(mongoCluster, "Cluster信息为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoCluster.getClusterName()), "ClusterName为空");
        Preconditions.checkArgument(regexMatcher.clusterName(mongoCluster.getClusterName()), "ClusterName不合法");
        checkClusterType(mongoCluster.getClusterType());
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoCluster.getDbName()), "DBName为空");
        Preconditions.checkArgument(regexMatcher.dbName(mongoCluster.getDbName()), "DbName不合法");
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoCluster.getUserId()), "UserId为空");
        Preconditions.checkArgument(regexMatcher.userId(mongoCluster.getUserId()), "UserId不合法");
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoCluster.getPassword()), "Password为空");
        Preconditions.checkArgument(regexMatcher.password(mongoCluster.getPassword()), "Password不合法");

        checkNodes(mongoCluster.getNodes());

        if (mongoCluster.getEnabled() == null) {
            mongoCluster.setEnabled(true);
        }
    }

    public void checkNodes(List<Node> nodes) {
        Preconditions.checkArgument(nodes != null && nodes.size() > 0, "Node信息为空");
        for (Node node : nodes) {
            checkNode(node);
        }
    }

    public void checkNode(Node node) {
        Preconditions.checkNotNull(node, "Node信息为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(node.getHost()), "Node的host为空");
        Preconditions.checkArgument(regexMatcher.host(node.getHost()), "Host不合法");
        Preconditions.checkNotNull(node.getPort(), "Node的port为空");
        Preconditions.checkArgument(regexMatcher.port(node.getPort().toString()), "Port不合法");
    }

    public void checkClusterType(String clusterType) {
        Preconditions.checkArgument(StringUtils.isNotBlank(clusterType), "ClusterType为空");
        Preconditions.checkArgument(ClusterType.REPLICATION.name().equalsIgnoreCase(clusterType) ||
                ClusterType.SHARDING.name().equalsIgnoreCase(clusterType), "ClusterType必须为replication或sharding");
    }
}
