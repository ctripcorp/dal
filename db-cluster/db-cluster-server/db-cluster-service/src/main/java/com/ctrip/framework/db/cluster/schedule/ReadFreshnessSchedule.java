package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.dto.*;
import com.ctrip.framework.db.cluster.entity.ShardInstance;
import com.ctrip.framework.db.cluster.enums.ShardInstanceHealthStatus;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.service.remote.mysqlapi.DBConnectionService;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ShardInstanceService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.ctrip.framework.db.cluster.util.thread.DefaultExecutorFactory.DEFAULT_KEEPER_ALIVE_TIME_SECONDS;

/**
 * Created by @author zhuYongMing on 2019/11/1.
 */
@Slf4j
@Component
public class ReadFreshnessSchedule {

    private static final String showSlaveCommand = "show slave status";

    private final static int default_threshold = 10;

    private BiConsumer<String, Integer> task;

    private final FreshnessScheduleRegistration registration;

    private final ScheduledExecutorService timer;

    private final ThreadPoolExecutor clusterThreadPool;

    private final ThreadPoolExecutor instanceThreadPool;

    @Resource
    private ClusterService clusterService;

    @Resource
    private ShardInstanceService shardInstanceService;

    @Resource
    private CipherService cipherService;

    @Resource
    private ConfigService configService;

    @Resource
    private DBConnectionService dbConnectionService;


    public ReadFreshnessSchedule() {
        this.registration = FreshnessScheduleRegistration.getRegistration();
        this.timer = Executors.newSingleThreadScheduledExecutor(new DalServiceThreadFactory("ReadFreshnessScheduleTimerThread"));
        this.clusterThreadPool = new ThreadPoolExecutor(
                8, 8, DEFAULT_KEEPER_ALIVE_TIME_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new DalServiceThreadFactory("ReadFreshnessScheduleClusterThread")
        );
        this.instanceThreadPool = new ThreadPoolExecutor(
                16, 16, DEFAULT_KEEPER_ALIVE_TIME_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new DalServiceThreadFactory("ReadFreshnessScheduleInstanceThread")
        );
        initTask();
        initSchedule();
    }

    public void executeTaskOnceTime(final String clusterName, final Integer thresholdSecond) {
        task.accept(clusterName, thresholdSecond);
    }

    private void initTask() {
        this.task = (clusterName, thresholdSecond) -> clusterThreadPool.submit(() -> {
            try {
                final ClusterDTO cluster = clusterService.findUnDeletedClusterDTO(clusterName);
                if (null == cluster) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadFreshnessSchedule] cluster is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final List<ZoneDTO> zones = cluster.getZones();
                if (CollectionUtils.isEmpty(zones)) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadFreshnessSchedule] zones is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final List<ShardDTO> shards = zones.stream().flatMap(
                        zone -> zone.getShards().stream()
                ).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(shards)) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadFreshnessSchedule] shards is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final List<ShardInstanceDTO> readInstances = shards.stream().flatMap(
                        shard -> shard.getReads().stream()
                ).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(readInstances)) {
                    registration.removeFromSchedule(clusterName);
                    log.info(String.format("[ReadFreshnessSchedule] read instances is not exists or deleted, " +
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
                    log.info(String.format("[ReadFreshnessSchedule] read user is not exists or deleted, " +
                            "remove this cluster from health schedule, clusterName = %s", clusterName));
                    return;
                }

                final CountDownLatch latch = new CountDownLatch(readInstances.size());
                final List<ShardInstanceDTO> pullInInstances = Lists.newArrayList();
                final List<ShardInstanceDTO> pulloutInstances = Lists.newArrayList();
                readInstances.forEach(instance -> instanceThreadPool.submit(() -> {
                    // freshness check
                    final String username = cipherService.decrypt(readUserOptional.get().getUsername());
                    final String password = cipherService.decrypt(readUserOptional.get().getPassword());
                    // url like : "jdbc:mysql://localhost:3306/mysql";
                    final String url = "jdbc:mysql://" + instance.getIp() +
                            ":" + instance.getPort() +
                            "/" + instance.getDbName() +
                            "?useSSL=false";

                    final PoolProperties properties = new PoolProperties();
                    properties.setUrl(url);
                    properties.setDriverClassName("com.mysql.jdbc.Driver");
                    properties.setUsername(username);
                    properties.setPassword(password);
                    properties.setMaxActive(1);
                    properties.setMaxIdle(1);
                    properties.setMinIdle(1);
                    properties.setInitialSize(1);
                    // 建立连接超时时间1s，等待请求返回超时时间threshold
                    final String connectionProperties = "connectTimeout=1000;socketTimeout=" + thresholdSecond * 1000;
                    properties.setConnectionProperties(connectionProperties);
                    final DataSource datasource = new DataSource();
                    datasource.setPoolProperties(properties);

                    try (final Connection connection = datasource.getConnection();
                         final Statement statement = connection.createStatement();
                         final ResultSet resultSet = statement.executeQuery(showSlaveCommand)) {

                        boolean ioThread = false;
                        boolean sqlThread = false;
                        Integer secondBehindMaster = Integer.MAX_VALUE;

                        if (resultSet.next()) {
                            ioThread = "YES".equalsIgnoreCase(resultSet.getString("Slave_IO_Running"));
                            sqlThread = "YES".equalsIgnoreCase(resultSet.getString("Slave_SQL_Running"));
                            secondBehindMaster = Integer.valueOf(resultSet.getString("Seconds_Behind_Master"));
                        }

                        if (ioThread && sqlThread && secondBehindMaster <= thresholdSecond) {
                            // pull in origin health status is un_enabled instance
                            if (instance.getShardInstanceHealthStatus() == ShardInstanceHealthStatus.un_enabled.getCode()) {
                                pullInInstances.add(instance);
                                // cat
                                final Map<String, String> pullInMap = Maps.newHashMapWithExpectedSize(1);
                                pullInMap.put("pull in reason", String.format("ioThread = true, sqlThread = true, secondBehindMaster = %d.", secondBehindMaster));
                                Cat.logEvent("Schedule.Read.Freshness.PullIn", clusterName + "-" + instance.getIp(), "waring", pullInMap);
                            }
                        } else {
                            // pullout origin health status is enabled instance
                            if (instance.getShardInstanceHealthStatus() == ShardInstanceHealthStatus.enabled.getCode()) {
                                pulloutInstances.add(instance);
                                // cat
                                final Map<String, String> pulloutMap = Maps.newHashMapWithExpectedSize(1);
                                pulloutMap.put("pullout reason", String.format("ioThread = %s, sqlThread = %s, secondBehindMaster = %d.", ioThread, sqlThread, secondBehindMaster));
                                Cat.logEvent("Schedule.Read.Freshness.Pullout", clusterName + "-" + instance.getIp(), "waring", pulloutMap);
                            }
                        }
                    } catch (Exception e) {
                        final String exceptionMessage = e.getMessage();
                        // exception : socketTimeout, Access denied, connect reject, connect timeout, Insufficient permissions
                        // pullout: socketTimeout
                        if (e instanceof CommunicationsException && e.getCause() instanceof SocketTimeoutException) {
                            if (instance.getShardInstanceHealthStatus() == ShardInstanceHealthStatus.enabled.getCode()) {
                                pulloutInstances.add(instance);
                                // cat
                                final Map<String, String> pulloutMap = Maps.newHashMapWithExpectedSize(1);
                                pulloutMap.put("pullout reason", String.format("socket timeout, exception = %s", e.toString()));
                                Cat.logEvent("Schedule.Read.Freshness.Pullout", clusterName + "-" + instance.getIp(), "waring", pulloutMap);
                            }
                        } else if (e instanceof SQLException && StringUtils.isNotBlank(exceptionMessage) && exceptionMessage.contains("Access denied for user")) {
                            // pullout: Access denied
                            if (instance.getShardInstanceHealthStatus() == ShardInstanceHealthStatus.enabled.getCode()) {
                                pulloutInstances.add(instance);
                                // cat
                                final Map<String, String> pulloutMap = Maps.newHashMapWithExpectedSize(1);
                                pulloutMap.put("pullout reason", String.format("Access denied, exception = %s", e.toString()));
                                Cat.logEvent("Schedule.Read.Freshness.Pullout", clusterName + "-" + instance.getIp(), "waring", pulloutMap);
                            }
                        } else {
                            // other
                            final Map<String, String> pulloutMap = Maps.newHashMapWithExpectedSize(1);
                            pulloutMap.put("pullout reason", String.format("other exception = %s", e.toString()));
                            Cat.logEvent("Schedule.Read.Freshness.Error", clusterName + "-" + instance.getIp(), "error", pulloutMap);
                        }
                        // pullout: connect reject

                        // check again: connect timeout
//                        if (e instanceof SQLException) {
//                            final boolean isValid = dbConnectionService.checkConnection();
//                            if (!isValid) {
//                                putout(instance, clusterName,
//                                        String.format(
//                                                ""
//                                        )
//                                );
//                            }
//                        }

                        // ignore: Insufficient permissions
//                        if (e instanceof SQLException) {
//                            // cat
//                        }
                    } finally {
                        datasource.close();
                    }
                    latch.countDown();
                }));

                try {
                    latch.await();
                    trig(pullInInstances, pulloutInstances, clusterName);
                } catch (InterruptedException e) {
                    // never, ignore
                }
            } catch (SQLException e) {
                log.error(String.format("ReadFreshnessSchedule running error, " +
                        "ignore this cluster health task, clusterName = %s", clusterName), e);
            }
        });
    }

    @DalTransactional(logicDbName = Constants.DATABASE_SET_NAME)
    void trig(final List<ShardInstanceDTO> pullInInstances,
              final List<ShardInstanceDTO> pulloutInstances,
              final String clusterName) {

        final List<ShardInstance> updatedInstance = Lists.newArrayListWithExpectedSize(
                pullInInstances.size() + pulloutInstances.size()
        );
        pullInInstances.forEach(instance -> {
            final ShardInstance updated = ShardInstance.builder()
                    .id(instance.getShardInstanceEntityId())
                    .healthStatus(ShardInstanceHealthStatus.enabled.getCode())
                    .build();
            updatedInstance.add(updated);
        });
        pulloutInstances.forEach(instance -> {
            final ShardInstance updated = ShardInstance.builder()
                    .id(instance.getShardInstanceEntityId())
                    .healthStatus(ShardInstanceHealthStatus.un_enabled.getCode())
                    .build();
            updatedInstance.add(updated);
        });

        if (CollectionUtils.isEmpty(updatedInstance)) {
            // do nothing
            return;
        }

        try {
            shardInstanceService.updateShardInstances(updatedInstance);
            clusterService.release(
                    Lists.newArrayList(clusterName),
                    Constants.HEALTH_SCHEDULE_OPERATOR,
                    Constants.RELEASE_TYPE_HEALTH_SCHEDULE_RELEASE
            );
        } catch (SQLException e) {
            log.error("[ReadFreshnessSchedule] release error.", e);
        }
    }

    private void initSchedule() {
        timer.scheduleWithFixedDelay(
                () -> {
                    if (configService.getFreshnessEnabled()) {
                        final Map<String, Integer> clusterThresholdSecondMap = configService.getFreshnessClusterEnabledAndThresholdSecond();
                        registration.getTargetClusters().forEach(clusterName -> {
                            Integer threshold = clusterThresholdSecondMap.get(clusterName);
                            if (null != threshold) {
                                // if threshold lte 0, default_threshold
                                threshold = threshold <= 0 ? default_threshold : threshold;
                                task.accept(clusterName, threshold);
                            }
                        });
                    }
                }, 1, 10, TimeUnit.SECONDS
        );
    }
}
