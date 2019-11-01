package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.domain.dto.*;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ShardInstanceService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private final Set<String> targetClusters = Sets.newCopyOnWriteArraySet();

    private Consumer<String> task;

    private final ScheduledExecutorService timer;

    private final ThreadPoolExecutor runnerThreadPool;

    private final ClusterService clusterService;

    private final ShardInstanceService shardInstanceService;


    public ReadHealthSchedule(final ClusterService clusterService,
                              final ShardInstanceService shardInstanceService) {
        this.timer = Executors.newSingleThreadScheduledExecutor(new DalServiceThreadFactory("ReadHealthScheduleThread"));
        this.runnerThreadPool = new ThreadPoolExecutor(
                16, 16, DEFAULT_KEEPER_ALIVE_TIME_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new DalServiceThreadFactory("ReadHealthScheduleRunnerThread")
        );
        this.clusterService = clusterService;
        this.shardInstanceService = shardInstanceService;
        initTask();
        initSchedule();
    }


    public void registryToSchedule(final String clusterName) {
        targetClusters.add(clusterName);
    }

    public void executeTaskOnceTime(final String clusterName) {
        task.accept(clusterName);
    }

    private void initTask() {
        this.task = clusterName ->
                runnerThreadPool.submit(
                        () -> {
                            try {
                                final ClusterDTO cluster = clusterService.findUnDeletedClusterDTO(clusterName);
                                if (null == cluster) {
                                    targetClusters.remove(clusterName);
                                    return;
                                }

                                final List<ZoneDTO> zones = cluster.getZones();
                                if (CollectionUtils.isEmpty(zones)) {
                                    targetClusters.remove(clusterName);
                                    return;
                                }

                                final List<ShardDTO> shards = zones.stream().flatMap(
                                        zone -> zone.getShards().stream()
                                ).collect(Collectors.toList());
                                if (CollectionUtils.isEmpty(shards)) {
                                    targetClusters.remove(clusterName);
                                    return;
                                }

                                final List<ShardInstanceDTO> readInstances = shards.stream().flatMap(
                                        shard -> shard.getReads().stream()
                                ).collect(Collectors.toList());
                                final Optional<UserDTO> readUserOptional = shards.stream().flatMap(
                                        shard -> shard.getUsers().stream().filter(
                                                user -> Constants.ROLE_READ.equalsIgnoreCase(user.getPermission())
                                                        && !Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag())
                                        )
                                ).findFirst();


                                if (CollectionUtils.isEmpty(readInstances) || !readUserOptional.isPresent()) {

                                    return;
                                }

                                // health check


                            } catch (SQLException e) {
                                log.error(String.format("find unDeleted cluster by clusterName error, " +
                                        "ignore this cluster health schedule, clusterName = %s", clusterName), e);
                            }
                        }
                );
    }

    private void initSchedule() {
        timer.scheduleAtFixedRate(
                () -> targetClusters.forEach(task),
                1, 10, TimeUnit.SECONDS
        );
    }
}
