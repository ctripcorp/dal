package com.ctrip.framework.db.cluster.service.checker;

import com.ctrip.framework.db.cluster.domain.DBConnectionCheckRequest;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.DBConnectionService;
import com.ctrip.framework.db.cluster.vo.dal.create.*;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.ctrip.framework.db.cluster.util.Constants.*;

/**
 * Created by shenjie on 2019/5/14.
 */
@Component
public class DBConnectionChecker {

    @Autowired
    private DBConnectionService dbConnectionService;

    public void checkDBConnection(ClusterVo cluster, String env) {
        List<DBConnectionCheckRequest> requests = buildRequests(cluster, env);
        checkConnections(requests);
    }

    protected List<DBConnectionCheckRequest> buildRequests(ClusterVo cluster, String env) {
        List<DBConnectionCheckRequest> requests = Lists.newArrayList();
        String dbType = cluster.getDbCategory();
        List<ShardVo> shards = cluster.deprGetShards();
        for (ShardVo shard : shards) {
            String dbName = shard.getDbName();
            Map<String, List<UserVo>> userGroup = getUserGroup(shard.deprGetUsers());
            DatabaseVo master = shard.getMaster();
            if (master != null) {
                List<DBConnectionCheckRequest> masterRequests = buildRequests(dbName, dbType, ROLE_MASTER, master, userGroup, env);
                requests.addAll(masterRequests);
            }

            DatabaseVo slave = shard.getSlave();
            if (slave != null) {
                List<DBConnectionCheckRequest> slaveRequests = buildRequests(dbName, dbType, ROLE_SLAVE, slave, userGroup, env);
                requests.addAll(slaveRequests);
            }

            DatabaseVo read = shard.getRead();
            if (read != null) {
                List<DBConnectionCheckRequest> readRequests = buildRequests(dbName, dbType, ROLE_READ, read, userGroup, env);
                requests.addAll(readRequests);
            }
        }
        return requests;
    }

    private void checkConnections(List<DBConnectionCheckRequest> requests) {
        for (DBConnectionCheckRequest request : requests) {
            boolean isValid = dbConnectionService.checkConnection(request);
            if (!isValid) {
                Cat.logEvent("DB.Cluster.Service.DBConnectionCheck.Failed", String.format("dbName:%s,host:%s", request.getDbName(), request.getHost()));
                throw new DBClusterServiceException(String.format("DB connection check result=false, dbName:%s,host:%s", request.getDbName(), request.getHost()));
            }
        }
    }

    private List<DBConnectionCheckRequest> buildRequests(String dbName, String dbType, String role, DatabaseVo database, Map<String, List<UserVo>> userGroup, String env) {
        List<DBConnectionCheckRequest> allRequests = Lists.newArrayList();
        String domain = database.getDomain();
        for (InstanceVo instance : database.getInstances()) {
            List<UserVo> users = userGroup.get(role);
            List<DBConnectionCheckRequest> requests = buildRequests(dbName, dbType, domain, instance, users, env);
            allRequests.addAll(requests);
        }
        return allRequests;
    }

    private List<DBConnectionCheckRequest> buildRequests(String dbName, String dbType, String domain, InstanceVo instance, List<UserVo> users, String env) {
        List<DBConnectionCheckRequest> requests = Lists.newArrayList();
        String ip = instance.getIp();
        int port = instance.getPort();
        for (UserVo user : users) {
            DBConnectionCheckRequest domainConnectionCheckRequest = buildRequest(dbName, dbType, domain, port, user, env);
            DBConnectionCheckRequest ipConnectionCheckRequest = buildRequest(dbName, dbType, ip, port, user, env);
            requests.add(domainConnectionCheckRequest);
            requests.add(ipConnectionCheckRequest);
        }
        return requests;
    }

    private DBConnectionCheckRequest buildRequest(String dbName, String dbType, String host, int port, UserVo user, String env) {
        DBConnectionCheckRequest dbConnectionCheckRequest = DBConnectionCheckRequest.builder()
                .dbName(dbName)
                .dbType(dbType)
                .host(host)
                .port(port)
                .user(user.getUsername())
                .password(user.getPassword())
                .env(env)
                .build();

        return dbConnectionCheckRequest;
    }

    private Map<String, List<UserVo>> getUserGroup(List<UserVo> users) {
        Map<String, List<UserVo>> userGroup = Maps.newHashMap();
        for (UserVo user : users) {
            if (USER_TAG_ETL.equalsIgnoreCase(user.getTag()) && OPERATION_READ.equalsIgnoreCase(user.getPermission())) {
                List<UserVo> slaveUsers = userGroup.get(ROLE_SLAVE);
                if (slaveUsers == null) {
                    slaveUsers = Lists.newArrayList();
                    userGroup.put(ROLE_SLAVE, slaveUsers);
                }
                slaveUsers.add(user);
            } else {
                if (OPERATION_WRITE.equalsIgnoreCase(user.getPermission())) {
                    List<UserVo> masterUsers = userGroup.get(ROLE_MASTER);
                    if (masterUsers == null) {
                        masterUsers = Lists.newArrayList();
                        userGroup.put(ROLE_MASTER, masterUsers);
                    }
                    masterUsers.add(user);
                } else {
                    List<UserVo> readUsers = userGroup.get(ROLE_READ);
                    if (readUsers == null) {
                        readUsers = Lists.newArrayList();
                        userGroup.put(ROLE_READ, readUsers);
                    }
                    readUsers.add(user);
                }
            }
        }
        return userGroup;
    }
}
