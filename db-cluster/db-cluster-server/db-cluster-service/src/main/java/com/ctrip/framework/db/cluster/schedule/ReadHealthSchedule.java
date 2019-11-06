package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.dto.*;
import com.ctrip.framework.db.cluster.entity.ShardInstance;
import com.ctrip.framework.db.cluster.enums.ShardInstanceHealthStatus;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ShardInstanceService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.ctrip.framework.db.cluster.util.thread.DefaultExecutorFactory.DEFAULT_KEEPER_ALIVE_TIME_SECONDS;

/**
 * Created by @author zhuYongMing on 2019/11/1.
 */
@Slf4j
//@Component
public class ReadHealthSchedule {

    private final static String mysql_latency_sp = "{call sp_getslavestatus}";

    private final static int delay_threshold = 10;

    private Consumer<String> task;

    private final ReadHealthRegistration registration;

    private final ScheduledExecutorService timer;

    private final ThreadPoolExecutor runnerThreadPool;

    @Resource
    private CipherService cipherService;

    @Resource
    private ClusterService clusterService;

    @Resource
    private ShardInstanceService shardInstanceService;


    public ReadHealthSchedule() {
        this.registration = ReadHealthRegistration.getRegistration();
        this.timer = Executors.newSingleThreadScheduledExecutor(new DalServiceThreadFactory("ReadHealthScheduleTimerThread"));
        this.runnerThreadPool = new ThreadPoolExecutor(
                16, 16, DEFAULT_KEEPER_ALIVE_TIME_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new DalServiceThreadFactory("ReadHealthScheduleRunnerThread")
        );
        initTask();
        initSchedule();
    }

    public void executeTaskOnceTime(final String clusterName) {
        task.accept(clusterName);
    }

    private void initTask() {
        this.task = clusterName -> runnerThreadPool.submit(() -> {
            try {
                final ClusterDTO cluster = clusterService.findUnDeletedClusterDTO(clusterName);
                if (null == cluster) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadHealthSchedule] cluster is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final List<ZoneDTO> zones = cluster.getZones();
                if (CollectionUtils.isEmpty(zones)) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadHealthSchedule] zones is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final List<ShardDTO> shards = zones.stream().flatMap(
                        zone -> zone.getShards().stream()
                ).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(shards)) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadHealthSchedule] shards is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final List<ShardInstanceDTO> readInstances = shards.stream().flatMap(
                        shard -> shard.getReads().stream()
                ).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(readInstances)) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadHealthSchedule] read instances is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final Optional<UserDTO> readUserOptional = shards.stream().flatMap(
                        shard -> shard.getUsers().stream().filter(
                                user -> Constants.ROLE_READ.equalsIgnoreCase(user.getPermission())
                                        && !Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag())
                        )
                ).findFirst();
                if (!readUserOptional.isPresent()) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadHealthSchedule] read user is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                readInstances.forEach(instance -> {
                    // health check
                    final String username = cipherService.decrypt(readUserOptional.get().getUsername());
                    final String password = cipherService.decrypt(readUserOptional.get().getPassword());
                    // url like : "jdbc:mysql://localhost:3306/mysqljdbc";
                    final String url = "jdbc:mysql://" + instance.getIp() +
                            ":" + instance.getPort() +
                            "/" + instance.getDbName();

                    Connection connection;
                    try {
                        // single connection
                        // TODO: 2019/11/4 socket timeout, update to connection pool
                        connection = DriverManager.getConnection(url, username, password);
                        DriverManager.setLoginTimeout(1);
                    } catch (SQLException e) {
                        log.error(String.format("[ReadHealthSchedule] unable to get connection, try again later, clusterName = %s, " +
                                "ip = %s, port = %s", clusterName, instance.getIp(), instance.getPort()), e);

                        // retry
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException ignore) {
                            // never, ignore
                        }

                        try {
                            // TODO: 2019/11/4 socket timeout
                            // TODO: 2019/11/5 connection check
                            connection = DriverManager.getConnection(url, username, password);
                            DriverManager.setLoginTimeout(1);
                        } catch (SQLException e1) {
                            log.error(String.format("[ReadHealthSchedule] unable to get connection second times, put out it, clusterName = %s, " +
                                    "ip = %s, port = %s", clusterName, instance.getIp(), instance.getPort()), e1);
                            // putout
                            try {
                                putout(instance, clusterName, "unable to get connection twice, when health schedule");
                            } catch (SQLException e2) {
                                // ignore
                            }
                            return;
                        }
                    }

                    if (null != connection) {
                        CallableStatement callableStatement = null;
                        ResultSet resultSet = null;

                        Integer delay = 0;
                        try {
                            callableStatement = connection.prepareCall(mysql_latency_sp);
                            callableStatement.execute();
                            resultSet = callableStatement.getResultSet();
                            final ResultSetMetaData metaData = resultSet.getMetaData();
                            if (resultSet.next()) {
                                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                    final String columnLabel = metaData.getColumnLabel(i);
                                    final Object value = resultSet.getObject(columnLabel);
                                    System.out.println("sp result : columnName = " + columnLabel + ", value = " + value);
                                    // TODO: 2019/11/5 delay = ...
                                }
                            }

                        } catch (SQLException e) {
                            log.error(String.format("[ReadHealthSchedule] call sp = %s fail, maybe sp error, or sp not exists, clusterName = %s, " +
                                    "ip = %s, port = %s", mysql_latency_sp, clusterName, instance.getIp(), instance.getPort()), e);
                        } finally {
                            try {
                                if (null != resultSet) {
                                    resultSet.close();
                                }
                                if (null != callableStatement) {
                                    callableStatement.close();
                                }
                                connection.close();
                            } catch (SQLException e) {
                                // ignore
                            }
                        }

                        try {
                            // TODO: 2019/11/3 if ... putout, else putin
                            putout(instance, clusterName, String.format("Master-slave replication delay exceeds threshold = %s s", delay_threshold));
                            putin(instance, clusterName, String.format("Master-slave replication delay is less than threshold = %s s", delay_threshold));
                        } catch (SQLException e) {
                            // ignore
                        }
                    }
                });
            } catch (SQLException e) {
                log.error(String.format("ReadHealthSchedule running error, " +
                        "ignore this cluster health task, clusterName = %s", clusterName), e);
            }
        });
    }

    @DalTransactional(logicDbName = Constants.DATABASE_SET_NAME)
    void putout(final ShardInstanceDTO instance, final String clusterName, final String reason) throws SQLException {
        if (instance.getShardInstanceHealthStatus() == ShardInstanceHealthStatus.un_enabled.getCode()) {
            // un_enabled now, do nothing.
            return;
        }

        final ShardInstance shardInstance = ShardInstance.builder()
                .id(instance.getShardInstanceEntityId())
                .healthStatus(ShardInstanceHealthStatus.un_enabled.getCode())
                .build();
        try {
            shardInstanceService.update(shardInstance);
            clusterService.release(
                    Lists.newArrayList(clusterName),
                    Constants.HEALTH_SCHEDULE_OPERATOR,
                    Constants.RELEASE_TYPE_HEALTH_SCHEDULE_RELEASE
            );

            // TODO: 2019/11/3 cat
            log.info(String.format("[ReadHealthSchedule] put out shard instance, reason : %s, clusterName = %s, " +
                            "ip = %s, port = %s", reason, clusterName, instance.getIp(), instance.getPort()));
        } catch (SQLException e) {
            log.error(String.format("[ReadHealthSchedule] put out shard instance error, clusterName = %s" +
                    "ip = %s, port = %s", clusterName, instance.getIp(), instance.getPort()), e);
            throw e;
        }
    }

    @DalTransactional(logicDbName = Constants.DATABASE_SET_NAME)
    void putin(final ShardInstanceDTO instance, final String clusterName, final String reason) throws SQLException {
        if (instance.getShardInstanceHealthStatus() == ShardInstanceHealthStatus.enabled.getCode()) {
            // enabled now, do nothing.
            return;
        }

        final ShardInstance shardInstance = ShardInstance.builder()
                .id(instance.getShardInstanceEntityId())
                .healthStatus(ShardInstanceHealthStatus.enabled.getCode())
                .build();
        try {
            shardInstanceService.update(shardInstance);
            clusterService.release(
                    Lists.newArrayList(clusterName),
                    Constants.HEALTH_SCHEDULE_OPERATOR,
                    Constants.RELEASE_TYPE_HEALTH_SCHEDULE_RELEASE
            );

            // TODO: 2019/11/3 cat
            log.info(String.format("[ReadHealthSchedule] put int shard instance, reason : %s, clusterName = %s, " +
                            "ip = %s, port = %s", reason, clusterName, instance.getIp(), instance.getPort()));
        } catch (SQLException e) {
            log.error(String.format("[ReadHealthSchedule] put int shard instance error, clusterName = %s" +
                    "ip = %s, port = %s", clusterName, instance.getIp(), instance.getPort()), e);
            throw e;
        }
    }

    private void initSchedule() {
        timer.scheduleWithFixedDelay(
                () -> registration.getTargetClusters().forEach(task),
                1, 10, TimeUnit.SECONDS
        );
    }
}
