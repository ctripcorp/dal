package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.domain.plugin.titan.page.TitanKeyPageResponse;
import com.ctrip.framework.db.cluster.domain.plugin.titan.page.TitanKeyPageSingleConnectionData;
import com.ctrip.framework.db.cluster.domain.plugin.titan.page.TitanKeyPageSingleData;
import com.ctrip.framework.db.cluster.entity.TitanKey;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.service.plugin.TitanPluginService;
import com.ctrip.framework.db.cluster.service.repository.TitanKeyService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 */
@Slf4j
@Component
public class TitanKeySynchronizeSchedule {

    private static final Integer initPageNo = 1;

    private static final Integer initTotalPage = 1;

    private final ScheduledExecutorService timer;

    private final TitanPluginService titanPluginService;

    private final TitanKeyService titanKeyService;

    private final ConfigService configService;


    public TitanKeySynchronizeSchedule(final TitanPluginService titanPluginService, final TitanKeyService titanKeyService,
                                       final ConfigService configService) {
        this.timer = Executors.newSingleThreadScheduledExecutor(
                new DalServiceThreadFactory("TitanKeySynchronizeScheduleTimerThread")
        );
        this.titanPluginService = titanPluginService;
        this.titanKeyService = titanKeyService;
        this.configService = configService;
        initSchedule();
    }

    private void initSchedule() {
        timer.scheduleWithFixedDelay(
                () -> {
                    log.info("start titanKeys synchronize schedule.");

                    int totalPage = initTotalPage;
                    final int pageSize = configService.getTitanKeySynchronizeSchedulePageSize();
                    for (int pageNo = initPageNo; pageNo <= totalPage; pageNo++) {
                        try {
                            final TitanKeyPageResponse titanKeyPageResponse = titanPluginService.pageQueryTitanKeys(
                                    pageNo, pageSize, Constants.ENV
                            );

                            if (titanKeyPageResponse.isLegal()) {
                                // consumer
                                consumerTitanKeys(titanKeyPageResponse.getData().getData());

                                // dynamic totalPage
                                totalPage = titanKeyPageResponse.getData().getTotalPage();
                            } else {
                                log.error(String.format(
                                        "titanKeys synchronize schedule page query titanKeys result illegal, status = %d, message = %s, result = %s",
                                        titanKeyPageResponse.getStatus(), titanKeyPageResponse.getMessage(), titanKeyPageResponse.getData())
                                );
                            }
                        } catch (Exception e) {
                            log.error(String.format("titanKeys synchronize schedule error, ignore this batch, pageNo = %d, pageSize = %d",
                                    pageNo, pageSize), e);
                        }
                    }

                    log.info("end titanKeys synchronize schedule.");
                }, 0, configService.getTitanKeySynchronizeScheduleDelayMinutes(), TimeUnit.MINUTES
        );
    }

    private void consumerTitanKeys(final List<TitanKeyPageSingleData> remoteKeys) {
        final List<String> remoteKeyNames = remoteKeys.stream()
//                .map(TitanKeyPageSingleData::getName)
                .map(TitanKeyPageSingleData::getTitanKey)
                .collect(Collectors.toList());

        try {
            final List<TitanKey> insertTitanKeys = Lists.newArrayList();
            final List<TitanKey> updateTitanKeys = Lists.newArrayList();
            final List<TitanKey> localTitanKeys = titanKeyService.queryByNamesAndSubEnv(remoteKeyNames, null);

            remoteKeys.forEach(remote -> {
                final Optional<TitanKey> localKey = localTitanKeys.stream().filter(
//                        local -> Objects.equals(remote.getName(), local.getName())
                        local -> Objects.equals(remote.getTitanKey(), local.getName())
                                && Objects.equals(remote.getSubEnv(), local.getSubEnv())
                ).findFirst();

                if (localKey.isPresent()) {
                    // compare
                    final TitanKey local = localKey.get();
                    final boolean identical = compareIdentical(remote, local);
                    if (!identical) {
                        // update
                        updateTitanKeys.add(toUpdateTitanKey(remote, local.getId()));
                    }
                } else {
                    // insert
                    insertTitanKeys.add(toInsertTitanKey(remote));
                }
            });

            if (!CollectionUtils.isEmpty(insertTitanKeys)) {
                titanKeyService.create(insertTitanKeys);
            }

            if (!CollectionUtils.isEmpty(updateTitanKeys)) {
                titanKeyService.update(updateTitanKeys);
            }

        } catch (SQLException e) {
            log.error(String.format("titanKeys synchronize schedule consumer titanKeys error, keyNames = %s",
                    remoteKeyNames.toString()), e);
        }
    }

    private boolean compareIdentical(final TitanKeyPageSingleData remote, final TitanKey local) {
        final TitanKeyPageSingleConnectionData connectionInfo = remote.getConnectionInfo();

        return Objects.equals(local.getName(), remote.getTitanKey())
//                Objects.equals(local.getName(), remote.getName())
                && Objects.equals(local.getSubEnv(), remote.getSubEnv())
                && Objects.equals(Enabled.getEnabled(local.getEnabled()), Enabled.getEnabled(remote.getEnabled()))
                && Objects.equals(local.getProviderName(), remote.getProviderName())
                && Objects.equals(local.getCreateUser(), remote.getCreateUser())
                && Objects.equals(local.getUpdateUser(), remote.getUpdateUser())
                && Objects.equals(local.getPermissions(), remote.getPermissions())
                && Objects.equals(local.getFreeVerifyIps(), remote.getFreeVerifyIpList())
                && Objects.equals(local.getFreeVerifyApps(), remote.getFreeVerifyAppIdList())
                && Objects.equals(local.getMhaLastUpdateTime(), remote.getMhaLastUpdateTime())
                && Objects.equals(local.getDomain(), connectionInfo.getServer())
                && Objects.equals(local.getIp(), connectionInfo.getServerIp())
                && Objects.equals(local.getPort(), connectionInfo.getPort())
                && Objects.equals(local.getDbName(), connectionInfo.getDbName())
                && Objects.equals(local.getExtParams(), connectionInfo.getExtParam());
    }

    private TitanKey toInsertTitanKey(final TitanKeyPageSingleData remote) {
        final TitanKeyPageSingleConnectionData connectionInfo = remote.getConnectionInfo();

        return TitanKey.builder()
//                .name(remote.getName())
                .name(remote.getTitanKey())
                .subEnv(remote.getSubEnv())
                .enabled(Enabled.getEnabled(remote.getEnabled()).getCode())
                .providerName(remote.getProviderName())
                .createUser(remote.getCreateUser())
                .updateUser(remote.getUpdateUser())
                .permissions(remote.getPermissions())
                .freeVerifyIps(remote.getFreeVerifyIpList())
                .freeVerifyApps(remote.getFreeVerifyAppIdList())
                .mhaLastUpdateTime(remote.getMhaLastUpdateTime())
                .domain(connectionInfo.getServer())
                .ip(connectionInfo.getServerIp())
                .port(connectionInfo.getPort())
                .dbName(connectionInfo.getDbName())
                .extParams(connectionInfo.getExtParam())
                .build();
    }

    private TitanKey toUpdateTitanKey(final TitanKeyPageSingleData remote, final Integer localKeyId) {
        final TitanKeyPageSingleConnectionData connectionInfo = remote.getConnectionInfo();

        return TitanKey.builder()
                .id(localKeyId)
//                .name(remote.getName())
                .name(remote.getTitanKey())
                .subEnv(remote.getSubEnv())
                .enabled(Enabled.getEnabled(remote.getEnabled()).getCode())
                .providerName(remote.getProviderName())
                .createUser(remote.getCreateUser())
                .updateUser(remote.getUpdateUser())
                .permissions(remote.getPermissions())
                .freeVerifyIps(remote.getFreeVerifyIpList())
                .freeVerifyApps(remote.getFreeVerifyAppIdList())
                .mhaLastUpdateTime(remote.getMhaLastUpdateTime())
                .domain(connectionInfo.getServer())
                .ip(connectionInfo.getServerIp())
                .port(connectionInfo.getPort())
                .dbName(connectionInfo.getDbName())
                .extParams(connectionInfo.getExtParam())
                .build();
    }
}
