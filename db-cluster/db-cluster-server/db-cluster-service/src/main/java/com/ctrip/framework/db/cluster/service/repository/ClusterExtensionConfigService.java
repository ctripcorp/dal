package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.ClusterExtensionConfigDao;
import com.ctrip.framework.db.cluster.entity.ClusterExtensionConfig;
import com.ctrip.framework.db.cluster.enums.Deleted;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by @author zhuYongMing on 2019/11/6.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ClusterExtensionConfigService {

    private final ClusterExtensionConfigDao clusterExtensionConfigDao;


    public void create(final List<ClusterExtensionConfig> addedConfigs) throws SQLException {
        clusterExtensionConfigDao.insert(addedConfigs);
    }

    public void update(final List<ClusterExtensionConfig> updatedConfigs) throws SQLException {
        clusterExtensionConfigDao.update(updatedConfigs);
    }

    public List<ClusterExtensionConfig> findUnDeletedConfigs(final Integer clusterId) throws SQLException {
        final ClusterExtensionConfig queryConfig = ClusterExtensionConfig.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .build();
        return clusterExtensionConfigDao.queryBy(queryConfig);
    }
}
