package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.vo.mongo.MongoClusterVo;
import com.ctrip.framework.db.cluster.vo.mongo.NodeVo;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by shenjie on 2019/3/19.
 */
@Component
public class MongoValidityChecker {

    @Autowired
    private RegexMatcher regexMatcher;

    public void checkMongoCluster(MongoClusterVo mongoClusterVo) {
        Preconditions.checkNotNull(mongoClusterVo, "Cluster信息为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoClusterVo.getClusterName()), "ClusterName为空");
        Preconditions.checkArgument(regexMatcher.clusterName(mongoClusterVo.getClusterName()), "ClusterName不合法");
        Preconditions.checkArgument(regexMatcher.clusterType(mongoClusterVo.getClusterType()), "ClusterType必须为Replication或Sharding");
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoClusterVo.getDbName()), "DBName为空");
        Preconditions.checkArgument(regexMatcher.dbName(mongoClusterVo.getDbName()), "DbName不合法");
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoClusterVo.getUserId()), "UserId为空");
        Preconditions.checkArgument(regexMatcher.userId(mongoClusterVo.getUserId()), "UserId不合法");
        Preconditions.checkArgument(StringUtils.isNotBlank(mongoClusterVo.getPassword()), "Password为空");
        Preconditions.checkArgument(regexMatcher.password(mongoClusterVo.getPassword()), "Password不合法");

        checkNodes(mongoClusterVo.getNodes());

        if (mongoClusterVo.getEnabled() == null) {
            mongoClusterVo.setEnabled(true);
        }
    }

    public void checkNodes(List<NodeVo> nodes) {
        Preconditions.checkArgument(nodes != null && nodes.size() > 0, "Node信息为空");
        for (NodeVo nodeVo : nodes) {
            checkNode(nodeVo);
        }
    }

    public void checkNode(NodeVo nodeVo) {
        Preconditions.checkNotNull(nodeVo, "Node信息为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(nodeVo.getHost()), "Node的host为空");
        Preconditions.checkArgument(regexMatcher.domain(nodeVo.getHost()) || regexMatcher.ip(nodeVo.getHost()), "Host不合法");
        Preconditions.checkNotNull(nodeVo.getPort(), "Node的port为空");
        Preconditions.checkArgument(regexMatcher.port(nodeVo.getPort().toString()), "Port不合法");
    }

}
