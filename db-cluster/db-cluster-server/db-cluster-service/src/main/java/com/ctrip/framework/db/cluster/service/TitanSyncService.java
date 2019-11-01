package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.domain.PluginStatusCode;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanKeyInfo;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanUpdateRequest;
import com.ctrip.framework.db.cluster.service.builder.TitanKeyBuilder;
import com.ctrip.framework.db.cluster.service.plugin.TitanPluginService;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.concurrent.NamedThreadFactory;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/5/14.
 */
@Slf4j
@Service
public class TitanSyncService {

    private final int CORE_POOL_SIZE = 4;
    private final int MAX_POOL_SIZE = 50;
    private final int QUEUE_SIZE = 1000;
    private ExecutorService executor;

    @Autowired
    private TitanPluginService titanPluginService;
    @Autowired
    private TitanKeyBuilder titanKeyBuilder;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 1L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(QUEUE_SIZE),
                new NamedThreadFactory("Titan-Sync-Worker"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void addTitanKeysAsync(ClusterVo cluster, String env) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    addTitanKeys(cluster, env);
                } catch (Exception e) {
                    log.error("Add dal cluster [" + cluster.getClusterName() + "] titan keys failed.", e);
                }
            }
        });
    }


    public void updateTitanKeysAsync(List<ShardVo> shardVos, String env, String operator) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    updateTitanKeys(shardVos, env, operator);
                } catch (SQLException e) {
                    log.error("Update titan keys failed.", e);
                }
            }
        });
    }

    protected void updateTitanKeys(List<ShardVo> shardVos, String env, String operator) throws SQLException {
        TitanUpdateRequest request = titanKeyBuilder.buildTitanUpdateRequest(shardVos, env);

        PluginResponse titanUpdateResponse = titanPluginService.switchTitanKey(request, operator);
        if (titanUpdateResponse != null && PluginStatusCode.OK != titanUpdateResponse.getStatus()) {
            log.error("Update titan keys to plugin failed, error message is {}", titanUpdateResponse.getMessage());
        }
    }

    protected void addTitanKeys(ClusterVo cluster, String env) {
        List<TitanKeyInfo> titanKeys = titanKeyBuilder.build(cluster);
        for (TitanKeyInfo titanKey : titanKeys) {
            addTitanKey(titanKey, env);
        }
    }

    private void addTitanKey(TitanKeyInfo titanKey, String env) {
        try {
            PluginResponse pluginResponse = titanPluginService.addTitanKey(titanKey, env);
            if (pluginResponse != null && PluginStatusCode.OK != pluginResponse.getStatus()) {
                log.error("Add titan key[{}] to plugin failed, error message is {}", titanKey.getKeyName(), pluginResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("Add titan key[" + titanKey.getKeyName() + "] to plugin failed.", e);
        }

    }

}
