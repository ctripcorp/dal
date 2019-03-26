package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.config.ConfigManager;
import com.ctrip.framework.db.cluster.domain.MongoClusterAddRequestBody;
import com.ctrip.framework.db.cluster.domain.Node;
import com.ctrip.framework.db.cluster.enums.ClusterType;
import com.ctrip.framework.db.cluster.enums.Env;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;


/**
 * Created by shenjie on 2019/3/19.
 */
public class ValidityChecker {

    public static boolean checkAllowedIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }

        Set<String> allowedIps = ConfigManager.getInstance().getAllowedIps();
        if (allowedIps == null) {
            return false;
        }

        return allowedIps.contains(ip);
    }

    public static String checkAndGetEnv(String env) {
        if (StringUtils.isBlank(env)) {
            return Env.PRO.name().toLowerCase();
        } else {
            return env.trim().toLowerCase();
        }
    }

    public static void checkOperator(String operator) {
        Preconditions.checkArgument(StringUtils.isNotBlank(operator), "Operator为空");
    }

    public static void checkMongoCluster(MongoClusterAddRequestBody requestBody) {
        Preconditions.checkNotNull(requestBody, "Cluster信息为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(requestBody.getClusterName()), "ClusterName为空");
        checkClusterType(requestBody.getClusterType());
        Preconditions.checkArgument(StringUtils.isNotBlank(requestBody.getDbName()), "DBName为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(requestBody.getUserId()), "UserId为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(requestBody.getPassword()), "Password为空");
        checkNodes(requestBody.getNodes());

        if (requestBody.getEnabled() == null) {
            requestBody.setEnabled(true);
        }
    }

    public static void checkNodes(List<Node> nodes) {
        Preconditions.checkArgument(nodes != null && nodes.size() > 0, "Node信息为空");
        for (Node node : nodes) {
            checkNode(node);
        }
    }

    public static void checkNode(Node node) {
        Preconditions.checkNotNull(node, "Node信息为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(node.getHost()), "Node的host为空");
        Preconditions.checkNotNull(node.getPort(), "Node的port为空");
    }

    public static void checkClusterType(String clusterType) {
        Preconditions.checkArgument(StringUtils.isNotBlank(clusterType), "ClusterType为空");
        Preconditions.checkArgument(ClusterType.REPLICATION.name().equalsIgnoreCase(clusterType) ||
                ClusterType.SHARDING.name().equalsIgnoreCase(clusterType), "ClusterType必须为replication或sharding");
    }
}
