package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.entity.*;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.service.remote.qconfig.QConfigService;
import com.ctrip.framework.db.cluster.service.remote.qconfig.domain.QConfigFileDetailResponse;
import com.ctrip.framework.db.cluster.service.repository.*;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.unidal.tuple.Triple;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/11/10.
 */
@Slf4j
@Component
public class TitanKeysUsageConsistencySchedule {

    private final ScheduledExecutorService timer;

    private final ClusterService clusterService;

    private final ShardService shardService;

    private final ShardInstanceService shardInstanceService;

    private final InstanceService instanceService;

    private final TitanKeyService titanKeyService;

    private final QConfigService qConfigService;


    public TitanKeysUsageConsistencySchedule(final ClusterService clusterService, final ShardService shardService,
                                             final ShardInstanceService shardInstanceService, final InstanceService instanceService,
                                             final TitanKeyService titanKeyService, final QConfigService qConfigService) {

        this.timer = Executors.newSingleThreadScheduledExecutor(
                new DalServiceThreadFactory("TitanKeyUsageConsistencyScheduleTimerThread")
        );
        this.clusterService = clusterService;
        this.shardService = shardService;
        this.shardInstanceService = shardInstanceService;
        this.instanceService = instanceService;
        this.titanKeyService = titanKeyService;
        this.qConfigService = qConfigService;
        initSchedule();
    }

    private void initSchedule() {
        timer.scheduleWithFixedDelay(() -> {
            try {
                final List<Integer> clusterIds = clusterService.findClusters(
                        null, Deleted.un_deleted, Enabled.enabled
                ).stream().map(Cluster::getId).collect(Collectors.toList());

                final List<Shard> shards = shardService.findShards(clusterIds, Deleted.un_deleted);

                final List<ShardInstance> masterShardInstances = shardInstanceService.findUnDeletedByShardIdsAndRole(
                        shards.stream().map(Shard::getId).collect(Collectors.toList()),
                        Constants.ROLE_MASTER
                );
                final List<Instance> masterInstances = instanceService.findUnDeletedByPks(
                        masterShardInstances.stream().map(ShardInstance::getInstanceId).collect(Collectors.toList())
                );

                final List<Triple<String, String, Integer>> domainIpPortTriples = Lists.newArrayList();
                shards.forEach(shard -> {
                    final String masterDomain = shard.getMasterDomain();
                    if (StringUtils.isNotBlank(masterDomain)) {
                        final Optional<ShardInstance> masterShardInstanceOptional = masterShardInstances.stream()
                                .filter(masterShardInstance -> shard.getId().equals(masterShardInstance.getShardId()))
                                .findFirst();
                        if (masterShardInstanceOptional.isPresent()) {
                            final Optional<Instance> masterInstanceOptional = masterInstances.stream()
                                    .filter(
                                            masterInstance -> masterShardInstanceOptional.get().getInstanceId()
                                                    .equals(masterInstance.getId())
                                    )
                                    .findFirst();
                            if (masterInstanceOptional.isPresent()) {
                                final Instance master = masterInstanceOptional.get();
                                final Triple<String, String, Integer> triple = new Triple<>(masterDomain, master.getIp(), master.getPort());
                                domainIpPortTriples.add(triple);
                            }
                        }
                    }
                });

                final List<String> domains = domainIpPortTriples.stream().map(Triple::getFirst).collect(Collectors.toList());
                final List<TitanKey> titanKeys = titanKeyService.findByDomains(domains, Enabled.enabled);

                titanKeys.forEach(titanKey -> {
                    final String titanKeyName = titanKey.getName();
                    final String subEnv = titanKey.getSubEnv();
                    try {
                        final QConfigFileDetailResponse detailResponse = qConfigService.queryFileDetail(titanKeyName, subEnv);
                        if (detailResponse.isLegal()) {
                            final String data = detailResponse.getData().getData();
                            final List<String> keyValuePairs = Lists.newArrayList(data.split("\n")).stream().map(String::trim).filter(
                                    pair -> pair.contains("serverName") || pair.contains("serverIp") ||
                                            pair.contains("port") || pair.contains("mhaLastUpdateTime")
                            ).collect(Collectors.toList());

                            String remoteDomain = "";
                            String remoteIp = "";
                            Integer remotePort = 0;
                            String mhaLastUpdateTime = "";
                            for (String pair : keyValuePairs) {
                                final String[] keyValuePair = pair.split("=");
                                if (keyValuePair.length == 2) {
                                    if ("serverName".equals(keyValuePair[0])) {
                                        remoteDomain = keyValuePair[1];
                                    }

                                    if ("serverIp".equals(keyValuePair[0])) {
                                        remoteIp = keyValuePair[1];
                                    }

                                    if ("port".equals(keyValuePair[0])) {
                                        remotePort = Integer.valueOf(keyValuePair[1]);
                                    }

                                    if ("mhaLastUpdateTime".equals(keyValuePair[0])) {
                                        mhaLastUpdateTime = keyValuePair[1];
                                    }
                                }
                            }

                            final Optional<Triple<String, String, Integer>> tripleOptional = domainIpPortTriples.stream()
                                    .filter(triple -> triple.getFirst().equals(titanKey.getDomain()))
                                    .findFirst();

                            if (tripleOptional.isPresent()) {
                                final Triple<String, String, Integer> triple = tripleOptional.get();
                                if (StringUtils.isBlank(remoteIp)) {
                                    // No previous dynamic data source switching, ip is blank, mhaLastUpdateTime is blank
                                    if (StringUtils.isBlank(mhaLastUpdateTime)) {
                                        if (!(remoteDomain.equals(triple.getFirst())
                                                && remotePort.equals(triple.getLast()))) {
                                            Cat.logEvent("Schedule.TitanKeys.Usage.Consistency.TitanKeyNotConsistency", titanKeyName + "-" + subEnv);
                                        }
                                    } else {
                                        Cat.logEvent("Schedule.TitanKeys.Usage.Consistency.TitanKeyNotConsistency", titanKeyName + "-" + subEnv);
                                    }
                                } else {
                                    if (!(remoteDomain.equals(triple.getFirst())
                                            && remoteIp.equals(triple.getMiddle())
                                            && remotePort.equals(triple.getLast()))) {
                                        Cat.logEvent("Schedule.TitanKeys.Usage.Consistency.TitanKeyNotConsistency", titanKeyName + "-" + subEnv);
                                    }
                                }
                            }

                        } else {
                            log.error(String.format(
                                    "titanKeys usage consistency schedule query titanKey detail result illegal, titanKey = %s, subEnv = %s, response = %s.",
                                    titanKeyName, subEnv, detailResponse.toString())
                            );
                        }
                    } catch (Exception e) {
                        log.error(String.format("titanKeys usage consistency schedule query titanKey detail error, titanKey = %s, subEnv = %s",
                                titanKeyName, subEnv), e);
                    }
                });
            } catch (SQLException e) {
                log.error("titanKeys usage consistency schedule error.", e);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
}
