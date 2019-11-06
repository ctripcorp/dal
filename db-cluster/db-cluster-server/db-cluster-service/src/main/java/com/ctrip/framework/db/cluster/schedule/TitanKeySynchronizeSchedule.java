package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.domain.plugin.titan.page.TitanKeyPageResponse;
import com.ctrip.framework.db.cluster.domain.plugin.titan.page.TitanKeyPageSingleData;
import com.ctrip.framework.db.cluster.entity.TitanKey;
import com.ctrip.framework.db.cluster.service.plugin.TitanPluginService;
import com.ctrip.framework.db.cluster.service.repository.TitanKeyService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 */
@Slf4j
//@Component
public class TitanKeySynchronizeSchedule {

    private static final Integer pageSize = 1000;

    private static final Integer initPageNo = 1;

    private static final Integer initTotalPage = 1;

    private final ScheduledExecutorService timer;

    private final TitanPluginService titanPluginService;

    private final TitanKeyService titanKeyService;


    public TitanKeySynchronizeSchedule(final TitanPluginService titanPluginService, final TitanKeyService titanKeyService) {
        this.timer = Executors.newSingleThreadScheduledExecutor(
                new DalServiceThreadFactory("TitanKeySynchronizeScheduleTimerThread")
        );
        this.titanPluginService = titanPluginService;
        this.titanKeyService = titanKeyService;
        initSchedule();
    }

    private void initSchedule() {
        timer.scheduleWithFixedDelay(
                () -> {
                    log.info("start titanKeys synchronize schedule.");

                    int totalPage = initTotalPage;
                    for (int pageNo = initPageNo; pageNo <= totalPage; pageNo++) {
                        try {
                            final TitanKeyPageResponse titanKeyPageResponse = titanPluginService.pageQueryTitanKeys(
                                    pageNo, pageSize, Constants.ENV
                            );

                            if (titanKeyPageResponse.isSuccess()) {
                                // consumer
                                consumerTitanKeys(titanKeyPageResponse.getData().getData());

                                // dynamic totalPage
                                totalPage = titanKeyPageResponse.getData().getTotalPage();
                            } else {
                                log.error(String.format("titanKeys synchronize schedule error, message = %s, pageNo = %d, pageSize = %d",
                                        titanKeyPageResponse.getMessage(), pageNo, pageSize));
                            }
                        } catch (Exception e) {
                            log.error(String.format("titanKeys synchronize schedule error, pageNo = %d, pageSize = %d",
                                    pageNo, pageSize), e);
                        }
                    }

                    // TODO: 2019/11/7  delete by mark column
                    log.info("end titanKeys synchronize schedule.");
                }, 1, 10, TimeUnit.MINUTES
        );
    }

    private void consumerTitanKeys(final List<TitanKeyPageSingleData> remoteKeys) {
        final List<String> remoteKeyNames = remoteKeys.stream()
                .map(TitanKeyPageSingleData::getName).collect(Collectors.toList());

        try {
            final List<TitanKey> insertTitanKeys = Lists.newArrayList();
            final List<TitanKey> updateTitanKeys = Lists.newArrayList();
            final List<TitanKey> localTitanKeys = titanKeyService.findTitanKeys(remoteKeyNames);

            remoteKeys.forEach(remote -> {
                final Optional<TitanKey> localKey = localTitanKeys.stream().filter(
                        local -> remote.getName().equals(local.getName())
                                && remote.getSubEnv().equals(local.getSubEnv())
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

            titanKeyService.create(insertTitanKeys);
            titanKeyService.update(updateTitanKeys);

        } catch (SQLException e) {
            log.error(String.format("titanKeys synchronize schedule consumer titanKeys error, keyNames = %s",
                    remoteKeyNames.toString()), e);
        }
    }

    private boolean compareIdentical(final TitanKeyPageSingleData remote, final TitanKey local) {

        return true;
    }

    private TitanKey toInsertTitanKey(final TitanKeyPageSingleData remote) {
        return TitanKey.builder()

                .build();
    }

    private TitanKey toUpdateTitanKey(final TitanKeyPageSingleData remote, final Integer localKeyId) {
        return TitanKey.builder()

                .build();
    }
}
