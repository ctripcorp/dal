package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.Enabled;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.TitanKeyService;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author zhuYongMing on 2019/11/10.
 */
@Slf4j
//@Component
public class TitanKeysUsageConsistencySchedule {

    private final ScheduledExecutorService timer;

    private final ClusterService clusterService;

    private final TitanKeyService titanKeyService;


    public TitanKeysUsageConsistencySchedule(final ClusterService clusterService, final TitanKeyService titanKeyService) {
        this.timer = Executors.newSingleThreadScheduledExecutor(
                new DalServiceThreadFactory("TitanKeyUsageConsistencyScheduleTimerThread")
        );
        this.clusterService = clusterService;
        this.titanKeyService = titanKeyService;
        initSchedule();
    }

    private void initSchedule() {
//        timer.scheduleWithFixedDelay(() -> {
//            clusterService.findClusters(
//                    null, Deleted.un_deleted, Enabled.enabled
//            );
//        }, 1, 1, TimeUnit.MINUTES);
    }
}
