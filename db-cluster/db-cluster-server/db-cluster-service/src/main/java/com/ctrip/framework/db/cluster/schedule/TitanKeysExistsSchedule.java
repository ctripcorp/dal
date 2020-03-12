package com.ctrip.framework.db.cluster.schedule;

import com.ctrip.framework.db.cluster.entity.TitanKey;
import com.ctrip.framework.db.cluster.service.remote.qconfig.QConfigService;
import com.ctrip.framework.db.cluster.service.remote.qconfig.domain.QConfigFileNameResponse;
import com.ctrip.framework.db.cluster.service.remote.qconfig.domain.QConfigSubEnvResponse;
import com.ctrip.framework.db.cluster.service.repository.TitanKeyService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.thread.DalServiceThreadFactory;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/11/7.
 */
@Slf4j
@Component
public class TitanKeysExistsSchedule {

    private final ScheduledExecutorService timer;

    private final TitanKeyService titanKeyService;

    private final QConfigService qConfigService;


    public TitanKeysExistsSchedule(final TitanKeyService titanKeyService, final QConfigService qConfigService) {
        this.timer = Executors.newSingleThreadScheduledExecutor(
                new DalServiceThreadFactory("TitanKeyExistsScheduleTimerThread")
        );
        this.titanKeyService = titanKeyService;
        this.qConfigService = qConfigService;
        initSchedule();
    }

    private void initSchedule() {
        timer.scheduleWithFixedDelay(() -> {
            log.info("start titanKeys exists schedule.");

            try {
                final QConfigSubEnvResponse subEnvResponse = qConfigService.querySubEnv(Constants.ENV);
                if (subEnvResponse.isLegal()) {
                    final List<String> subEnv = subEnvResponse.getData();
                    subEnv.add(""); // add parent env

                    // Map<subEnv, List<keyName>>
                    final Map<String, List<String>> subEnvAndKeyNames = Maps.newHashMap();
                    subEnv.forEach(sub -> {
                        final QConfigFileNameResponse fileNameResponse = qConfigService.queryFileNames(sub);
                        if (fileNameResponse.isLegal()) {
                            subEnvAndKeyNames.put(
                                    sub.toLowerCase(),
                                    fileNameResponse.getData().getNormal().stream()
                                            .map(String::toLowerCase).collect(Collectors.toList())
                            );
                        } else {
                            log.error(String.format(
                                    "titanKeys exists schedule query fileNames result illegal, subEnv = %s, status = %d, message = %s, result = %s",
                                    sub, fileNameResponse.getStatus(), fileNameResponse.getMessage(), fileNameResponse.getData())
                            );
                        }
                    });

                    // compare count and keyNames
                    compare(subEnvAndKeyNames);
                } else {
                    log.error(String.format(
                            "titanKeys exists schedule query subEnv result illegal, status = %d, message = %s, result = %s",
                            subEnvResponse.getStatus(), subEnvResponse.getMessage(), subEnvResponse.getData())
                    );
                }
            } catch (Exception e) {
                log.error("titanKeys exists schedule remote call qconfig error.", e);
            }

            log.info("end titanKeys exists schedule.");
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void compare(final Map<String, List<String>> remoteSubEnvAndKeyNames) {
        try {
            final List<TitanKey> titanKeys = titanKeyService.findKeyNameAndSubEnv();
            final Map<String, List<String>> existSubEnvAndKeyNames = titanKeys.stream().collect(
                    Collectors.groupingBy(
                            titanKey -> titanKey.getSubEnv().toLowerCase(),
                            Collectors.mapping(
                                    titanKey -> titanKey.getName().toLowerCase(), Collectors.toList()
                            )
                    )
            );

            final Map<String, List<String>> excessSubEnvAndKeyNames = Maps.newHashMap();
            final Map<String, List<String>> missingSubEnvAndKeyNames = Maps.newHashMap();
            remoteSubEnvAndKeyNames.forEach((subEnv, remoteKeyNames) -> {
                // each subEnv
                List<String> existsKeyNames = existSubEnvAndKeyNames.get(subEnv);
                if (CollectionUtils.isEmpty(existsKeyNames)) {
                    if (!CollectionUtils.isEmpty(remoteKeyNames)) {
                        missingSubEnvAndKeyNames.put(subEnv, remoteKeyNames);
                    }
                    return;
                }

                // remoteKeyNames sort
                remoteKeyNames = remoteKeyNames.stream().sorted().collect(Collectors.toList());
                // existsKeyNames sort
                existsKeyNames = existsKeyNames.stream().sorted().collect(Collectors.toList());

                // compare
                if (!existsKeyNames.equals(remoteKeyNames)) {
                    // excess
                    final List<String> excess = Lists.newArrayList(existsKeyNames);
                    excess.removeAll(remoteKeyNames);
                    if (!CollectionUtils.isEmpty(excess)) {
                        excessSubEnvAndKeyNames.put(subEnv, excess);
                    }

                    // missing
                    final List<String> missing = Lists.newArrayList(remoteKeyNames);
                    missing.removeAll(existsKeyNames);
                    if (!CollectionUtils.isEmpty(missing)) {
                        missingSubEnvAndKeyNames.put(subEnv, missing);
                    }
                }
                existSubEnvAndKeyNames.remove(subEnv);
            });

            if (!CollectionUtils.isEmpty(existSubEnvAndKeyNames)) {
                existSubEnvAndKeyNames.forEach(excessSubEnvAndKeyNames::put);
            }

            if (!CollectionUtils.isEmpty(excessSubEnvAndKeyNames)) {
                final Map<String, String> nameValuePairs = Maps.newHashMapWithExpectedSize(1);
                nameValuePairs.put("excess keys", excessSubEnvAndKeyNames.toString());
                Cat.logEvent("Schedule.TitanKeys.Exists", "excess", "warning", nameValuePairs);
                log.error(String.format("titanKey exists schedule, excess keys = %s", excessSubEnvAndKeyNames.toString()));
            }

            if (!CollectionUtils.isEmpty(missingSubEnvAndKeyNames)) {
                final Map<String, String> nameValuePairs = Maps.newHashMapWithExpectedSize(1);
                nameValuePairs.put("missing keys", missingSubEnvAndKeyNames.toString());
                Cat.logEvent("Schedule.TitanKeys.Exists", "missing", "warning", nameValuePairs);
                log.error(String.format("titanKey exists schedule, missing keys = %s", missingSubEnvAndKeyNames.toString()));
            }

        } catch (SQLException e) {
            log.error("titanKeys exists schedule compare keyNames error.", e);
        }
    }
}