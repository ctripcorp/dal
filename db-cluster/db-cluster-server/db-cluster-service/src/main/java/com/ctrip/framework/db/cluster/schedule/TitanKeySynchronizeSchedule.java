package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.domain.plugin.titan.page.TitanKeyPageResponse;
import com.ctrip.framework.db.cluster.service.plugin.TitanPluginService;
import com.ctrip.framework.db.cluster.service.repository.TitanKeyService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 */
@Slf4j
@Component
public class TitanKeySynchronizeSchedule {

    private static final Integer pageSize = 100;

    private static final Integer initPageNo = 1;

    private final ScheduledExecutorService timer;

    private final TitanPluginService titanPluginService;

    private final TitanKeyService titanKeyService;


    public TitanKeySynchronizeSchedule(final TitanPluginService titanPluginService, final TitanKeyService titanKeyService) {
        this.timer = Executors.newSingleThreadScheduledExecutor(new DalServiceThreadFactory("TitanKeySynchronizeScheduleTimerThread"));
        this.titanPluginService = titanPluginService;
        this.titanKeyService = titanKeyService;
        initSchedule();
    }

    private void initSchedule() {
        timer.scheduleWithFixedDelay(
                () ->{

                    try {
                        final TitanKeyPageResponse titanKeyPageResponse = titanPluginService.pageQueryTitanKeys(initPageNo, pageSize, Constants.ENV);
//                        titanKeyPageResponse.
                    } catch (Exception e) {

                    }









                }, 1, 60, TimeUnit.SECONDS
        );
    }
}
