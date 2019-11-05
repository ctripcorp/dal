package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.ClusterExtensionConfigDao;
import com.ctrip.framework.db.cluster.entity.ClusterExtensionConfig;
import com.ctrip.framework.db.cluster.enums.ClusterExtensionConfigType;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterExtensionConfigVo;
import com.google.common.collect.Lists;
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


    public void create(final Integer clusterId, final List<ClusterExtensionConfigVo> addedConfigs) throws SQLException {
        final List<ClusterExtensionConfig> configs = Lists.newArrayListWithExpectedSize(addedConfigs.size());
        addedConfigs.forEach(addedConfig -> {
            final ClusterExtensionConfig config = ClusterExtensionConfig.builder()
                    .clusterId(clusterId)
                    .content(addedConfig.getContent())
                    .type(ClusterExtensionConfigType.getTypeCode(addedConfig.getTypeName()))
                    .build();
            configs.add(config);
        });
        clusterExtensionConfigDao.insert(configs);
    }

    public List<ClusterExtensionConfig> findUnDeletedConfigs(final Integer clusterId) throws SQLException {
        final ClusterExtensionConfig queryConfig = ClusterExtensionConfig.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .build();
        return clusterExtensionConfigDao.queryBy(queryConfig);
    }
}
