package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.dao.ClusterDao;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.entity.*;
import com.ctrip.framework.db.cluster.service.remote.mysqlapi.domain.DBConnectionCheckRequest;
import com.ctrip.framework.db.cluster.domain.dto.*;
import com.ctrip.framework.db.cluster.domain.plugin.dal.ReleaseCluster;
import com.ctrip.framework.db.cluster.domain.plugin.dal.ReleaseDatabase;
import com.ctrip.framework.db.cluster.domain.plugin.dal.ReleaseShard;
import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.TitanKeyMhaUpdateData;
import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.TitanKeyMhaUpdateRequest;
import com.ctrip.framework.db.cluster.enums.ClusterExtensionConfigType;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.Enabled;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.schedule.FreshnessScheduleRegistration;
import com.ctrip.framework.db.cluster.service.remote.mysqlapi.DBConnectionService;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.service.plugin.DalPluginService;
import com.ctrip.framework.db.cluster.service.plugin.TitanPluginService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.switches.ClusterSwitchesVo;
import com.ctrip.framework.db.cluster.vo.dal.switches.DatabaseSwitchesVo;
import com.ctrip.framework.db.cluster.vo.dal.switches.InstanceSwitchedVo;
import com.ctrip.framework.db.cluster.vo.dal.switches.ShardSwitchesVo;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.unidal.tuple.Pair;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ctrip.framework.db.cluster.enums.ClusterExtensionConfigType.id_generators;
import static com.ctrip.framework.db.cluster.enums.ClusterExtensionConfigType.shards_strategies;

/**
 * Created by shenjie on 2019/3/5.
 */
@Slf4j
@Service
public class ClusterService {

    @Resource
    private ClusterDao clusterDao;

    @Resource
    private ClusterSetService clusterSetService;

    @Resource
    private ShardService shardService;

    @Resource
    private InstanceService instanceService;

    @Resource
    private ShardInstanceService shardInstanceService;

    @Resource
    private TitanKeyService titanKeyService;

    @Resource
    private ClusterExtensionConfigService clusterExtensionConfigService;

    @Resource
    private CipherService cipherService;

    @Resource
    private DalPluginService dalPluginService;

    @Resource
    private TitanPluginService titanPluginService;

    @Resource
    private RegexMatcher regexMatcher;

    @Resource
    private ConfigService configService;

    @Resource
    private DBConnectionService dbConnectionService;


    @DalTransactional(logicDbName = Constants.DATABASE_SET_NAME)
    public void createCluster(final ClusterDTO clusterDTO) throws SQLException {
        final String clusterName = clusterDTO.getClusterName();
        final Cluster cluster = findCluster(
                clusterName, Deleted.un_deleted, null
        );
        if (null == cluster) {
            // not exists
            KeyHolder keyHolder = new KeyHolder();
            // create cluster
            clusterDao.insertWithKeyHolder(
                    keyHolder,
                    Cluster.builder()
                            .clusterName(clusterName)
                            .dbCategory(clusterDTO.getDbCategory())
                            .enabled(Enabled.enabled.getCode())
                            .deleted(Deleted.un_deleted.getCode())
                            .releaseTime(new Timestamp(1000))
                            .releaseVersion(0)
                            .build()
            );

            // construct cluster_sets
            final Integer clusterId = keyHolder.getKey().intValue();
            clusterDTO.setClusterEntityId(clusterId);
            clusterDTO.getZones().forEach(zoneDTO -> {
                zoneDTO.setClusterEntityId(clusterId);
                zoneDTO.getShards().forEach(shardDTO -> shardDTO.setClusterEntityId(clusterId));
            });

        } else {
            // exists
            throw new IllegalArgumentException(String.format("cluster name %s already exists.", clusterName));
        }

        // create cluster_sets
        final List<ZoneDTO> zoneDTOs = clusterDTO.getZones();
        if (!CollectionUtils.isEmpty(zoneDTOs)) {
            clusterSetService.createClusterSets(zoneDTOs);
        }
    }

    public ClusterDTO findUnDeletedClusterDTO(final String clusterName) throws SQLException {
        final Cluster cluster = findCluster(
                clusterName, Deleted.un_deleted, null
        );
        if (null == cluster) {
            return null;
        }

        final Integer clusterId = cluster.getId();

        // cluster extension configs
        final List<ClusterExtensionConfig> configs = clusterExtensionConfigService.findUnDeletedConfigs(clusterId);
        // zones
        final List<ZoneDTO> zones = clusterSetService.findUnDeletedByClusterId(clusterId);
        return componentClusterDTO(cluster, zones, configs);
    }

    public ClusterDTO findEffectiveClusterDTO(final String clusterName) throws SQLException {
        final Cluster cluster = findCluster(
                clusterName, Deleted.un_deleted, Enabled.enabled
        );
        if (null == cluster) {
            return null;
        }

        final Integer clusterId = cluster.getId();

        // cluster extension configs
        final List<ClusterExtensionConfig> configs = clusterExtensionConfigService.findUnDeletedConfigs(clusterId);
        // zones
        final List<ZoneDTO> zones = clusterSetService.findEffectiveByClusterId(clusterId);
        return componentClusterDTO(cluster, zones, configs);
    }

    private ClusterDTO componentClusterDTO(final Cluster cluster, final List<ZoneDTO> zones,
                                           final List<ClusterExtensionConfig> configs) {
        return ClusterDTO.builder()
                .clusterEntityId(cluster.getId())
                .clusterName(cluster.getClusterName())
                .dbCategory(cluster.getDbCategory())
                .clusterEnabled(cluster.getEnabled())
                .clusterDeleted(cluster.getDeleted())
                .clusterCreateTime(cluster.getCreateTime())
                .clusterReleaseTime(cluster.getReleaseTime())
                .clusterReleaseVersion(cluster.getReleaseVersion())
                .clusterUpdateTime(cluster.getUpdateTime())
                .zones(zones)
                .configs(configs)
                .build();
    }

    public Cluster findCluster(final String clusterName,
                               final Deleted deleted,
                               final Enabled enabled) throws SQLException {
        final List<Cluster> clusters = findClusters(Lists.newArrayList(clusterName), deleted, enabled);
        if (CollectionUtils.isEmpty(clusters)) {
            return null;
        } else {
            return clusters.get(0);
        }
    }

    public List<Cluster> findClusters(final List<String> clusterNames,
                                      final Deleted deleted,
                                      final Enabled enabled) throws SQLException {
        return clusterDao.findClusters(clusterNames, deleted, enabled);
    }


    @DalTransactional(logicDbName = Constants.DATABASE_SET_NAME)
    public void release(final List<String> clusterNames, final String operator, final String releaseType) throws SQLException {
        // valid
        final List<ClusterDTO> clusterDTOs = Lists.newArrayListWithExpectedSize(clusterNames.size());
        clusterNames.forEach(clusterName -> {
            try {
                final ClusterDTO cluster = findEffectiveClusterDTO(clusterName);
                if (null == cluster) {
                    throw new IllegalStateException(String.format("cluster is not exists, clusterName = %s", clusterName));
                }
                releaseValid(cluster);
                clusterDTOs.add(cluster);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // call dal plugin, if request fail, throw exception, transaction rollback.
        final List<ReleaseCluster> releaseClusters = clusterDTOs.stream()
                .map(cluster -> constructReleaseCluster(cluster, releaseType))
                .collect(Collectors.toList());
        final PluginResponse response = dalPluginService.releaseClusters(releaseClusters, Constants.ENV, operator);
        log.info(String.format("Release Dal Cluster: %s. Result Code: %s; Result Msg: %s", clusterNames.toString(),
                response.getStatus(), response.getMessage()));

        // update db
        final List<Cluster> clusters = Lists.newArrayListWithExpectedSize(clusterDTOs.size());
        clusterDTOs.forEach(clusterDTO -> {
            final Cluster updatedCluster = Cluster.builder()
                    .id(clusterDTO.getClusterEntityId())
                    .releaseVersion(clusterDTO.getClusterReleaseVersion() + 1)
                    .releaseTime(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            clusters.add(updatedCluster);
        });

        clusterDao.update(clusters);

        // registry read freshness schedule
        FreshnessScheduleRegistration.getRegistration().registryToSchedule(clusterNames);
    }

    private void releaseValid(final ClusterDTO cluster) {
        final String clusterName = cluster.getClusterName();

        // zones exists
        final List<ZoneDTO> zones = cluster.getZones();
        if (CollectionUtils.isEmpty(zones)) {
            throw new IllegalStateException(String.format("zones is empty, clusterName = %s", clusterName));
        }

        // zoneId
        if (zones.stream().map(ZoneDTO::getZoneId).distinct().count() != zones.size()) {
            throw new IllegalStateException(
                    String.format("zoneId不允许相同, zoneId比较是否相同不区分大小写, clusterName = %s", clusterName)
            );
        }
        // todo 目前每个cluster仅支持一个zone
        if (zones.size() != 1) {
            throw new IllegalStateException(
                    String.format("目前每个cluster仅支持一个zone, clusterName = %s", clusterName)
            );
        }

        // extension configs
        final List<ClusterExtensionConfig> configs = cluster.getConfigs();
        if (!CollectionUtils.isEmpty(configs)) {
            // duplicate config type
            Preconditions.checkArgument(
                    configs.stream().map(ClusterExtensionConfig::getType).distinct().count() == configs.size(),
                    String.format("cluster仅支持两类额外配置, shardStrategies和idGenerators, 且每个cluster每类配置的数量最多为1条, clusterName = %s", clusterName)
            );

            if (configs.size() > 2) {
                throw new IllegalStateException(
                        String.format("cluster仅支持两类额外配置, shardStrategies和idGenerators, " +
                                "且每个cluster每类配置的数量最多为1条, clusterName = %s", clusterName)
                );
            } else {
                configs.forEach(config ->
                        Preconditions.checkArgument(
                                Lists.newArrayList(ClusterExtensionConfigType.values()).
                                        stream().map(ClusterExtensionConfigType::getCode)
                                        .collect(Collectors.toList()).contains(config.getType()),
                                "cluster仅支持两类额外配置, shardStrategies和idGenerators.")
                );
            }
        }

        zones.forEach(zoneDTO -> {
            // shards exists
            final String zoneId = zoneDTO.getZoneId();
            final List<ShardDTO> shards = zoneDTO.getShards();
            if (CollectionUtils.isEmpty(shards)) {
                throw new IllegalStateException(
                        String.format("shards is empty, clusterName = %s, zoneId = %s", clusterName, zoneId)
                );
            }

            // shardIndex
            final List<Integer> sortedDistinctShardIndexes = shards.stream().map(ShardDTO::getShardIndex)
                    .distinct().sorted()
                    .collect(Collectors.toList());
            if (sortedDistinctShardIndexes.size() != shards.size()) {
                throw new IllegalStateException(
                        String.format("每个zone下, shardIndex不允许相同, clusterName = %s, zoneId = %s", clusterName, zoneId)
                );
            }
            final int lastShardIndex = shards.size() - 1;
            if (sortedDistinctShardIndexes.get(lastShardIndex) != lastShardIndex) {
                throw new IllegalStateException(
                        String.format(
                                "每个zone下, 所有shardIndex必须从0开始, 且为连续数字, 譬如:[0,1,2,3,4,5,6,7], clusterName = %s, zoneId = %s",
                                clusterName, zoneId
                        )
                );
            }

            // dbName
            if (shards.stream().map(ShardDTO::getDbName).distinct().count() != shards.size()) {
                throw new IllegalStateException(
                        String.format(
                                "每个zone下, dbName不允许相同, dbName比较是否相同不区分大小写, clusterName = %s, zoneId = %s",
                                clusterName, zoneId
                        )
                );
            }

            shards.forEach(shardDTO -> {
                // Master, Slave, Read valid
                final ShardInstanceDTO master = shardDTO.getMaster();
                final List<ShardInstanceDTO> slaves = shardDTO.getSlaves();
                final List<ShardInstanceDTO> reads = shardDTO.getReads();
                int shardInstanceCount = 0;
                if (null != master) {
                    shardInstanceCount += 1;
                }
                if (null != slaves) {
                    shardInstanceCount += slaves.size();
                }
                if (null != reads) {
                    shardInstanceCount += reads.size();
                }

                if (shardInstanceCount == 0) {
                    throw new IllegalStateException(
                            String.format(
                                    "每个shard至少存在任意一个节点, clusterName = %s, zoneId = %s, shardIndex = %s",
                                    clusterName, zoneId, shardDTO.getShardIndex()
                            )
                    );
                }

                // user
                final List<UserDTO> users = shardDTO.getUsers();
                if (CollectionUtils.isEmpty(users)) {
                    throw new IllegalStateException(
                            String.format(
                                    "每个shard至少存在一个user, clusterName = %s, zoneId = %s, shardIndex = %s",
                                    clusterName, zoneId, shardDTO.getShardIndex()
                            )
                    );
                }
                // TODO: 2019/10/28 暂时write/read user maxsize == 1, 不允许存在etl账号
                final List<UserDTO> writeUsers = users.stream().filter(
                        user -> Constants.OPERATION_WRITE.equalsIgnoreCase(user.getPermission())
                ).collect(Collectors.toList());

                final List<UserDTO> readUsers = users.stream().filter(
                        user -> Constants.OPERATION_READ.equalsIgnoreCase(user.getPermission())
                                && !Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag())
                ).collect(Collectors.toList());


                final List<UserDTO> etlUsers = users.stream().filter(
                        user -> Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag())
                                && Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag())
                ).collect(Collectors.toList());

                if (writeUsers.size() > 1) {
                    throw new IllegalStateException(
                            String.format(
                                    "每个shard最多只能存在一个write账号, clusterName = %s, zoneId = %s, shardIndex = %s",
                                    clusterName, zoneId, shardDTO.getShardIndex()
                            )
                    );
                }

                if (readUsers.size() > 1) {
                    throw new IllegalStateException(
                            String.format(
                                    "每个shard最多只能存在一个read账号, clusterName = %s, zoneId = %s, shardIndex = %s",
                                    clusterName, zoneId, shardDTO.getShardIndex()
                            )
                    );
                }

                if (etlUsers.size() > 0) {
                    throw new IllegalStateException(
                            String.format(
                                    "暂时不支持etl账号, clusterName = %s, zoneId = %s, shardIndex = %s",
                                    clusterName, zoneId, shardDTO.getShardIndex()
                            )
                    );
                }
            });
        });
    }


    private ReleaseCluster constructReleaseCluster(final ClusterDTO cluster, final String releaseType) {
        final List<ReleaseShard> shardRequests = Lists.newArrayList();

        // todo 暂时只有一个zone
        cluster.getZones().get(0).getShards().forEach(shard -> {
            // TODO: 2019/11/1 write, read账号目前只有一个
            final Optional<UserDTO> writeUserOptional = shard.getUsers().stream()
                    .filter(user -> Constants.OPERATION_WRITE.equalsIgnoreCase(user.getPermission()))
                    .findFirst();

            final Optional<UserDTO> readUserOptional = shard.getUsers().stream()
                    .filter(user -> Constants.OPERATION_READ.equalsIgnoreCase(user.getPermission())
                            && !Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag()))
                    .findFirst();

            // TODO: 2019/10/27 暂时不考虑etl user
//            final Optional<UserDTO> etlUserOptional = shard.getUsers().stream()
//                    .filter(user -> Constants.OPERATION_READ.equalsIgnoreCase(user.getPermission())
//                            && Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag()))
//                    .findFirst();

            if (!writeUserOptional.isPresent() && !readUserOptional.isPresent()) {
                throw new IllegalStateException(
                        String.format(
                                "每个shard至少存在一个write或read权限, 且非etl的账号, clusterName = %s, shardIndex = %s",
                                cluster.getClusterName(), shard.getShardIndex()
                        )
                );
            }

            // construct databaseRequest
            final List<ReleaseDatabase> databaseRequests = Lists.newArrayList();
            // construct connect check requests,
            final List<DBConnectionCheckRequest> connectionRequests = Lists.newArrayList();

            // write user
            if (writeUserOptional.isPresent()) {
                final UserDTO writeUser = writeUserOptional.get();
                final ShardInstanceDTO master = shard.getMaster();
                if (null == master) {
                    throw new IllegalStateException(
                            String.format(
                                    "shard中如果存在write权限账号, 必须存在Master角色的节点信息, clusterName = %s, shardIndex = %s",
                                    cluster.getClusterName(), shard.getShardIndex())
                    );
                } else {
                    final String username = cipherService.decrypt(writeUser.getUsername());
                    final String password = cipherService.decrypt(writeUser.getPassword());
                    final ReleaseDatabase databaseRequest = ReleaseDatabase.builder()
                            .ip(master.getIp())
                            .port(master.getPort())
                            .role(Constants.ROLE_MASTER)
                            .dbName(master.getDbName())
                            .uid(username)
                            .password(password)
                            .readWeight(master.getReadWeight())
                            .build();
                    databaseRequests.add(databaseRequest);

                    final DBConnectionCheckRequest domainRequest = constructConnectionCheckRequest(
                            shard.getMasterDomain(), shard.getMasterPort(), cluster.getDbCategory(),
                            username, password, shard.getDbName()
                    );
                    final DBConnectionCheckRequest ipRequest = constructConnectionCheckRequest(
                            master.getIp(), master.getPort(), cluster.getDbCategory(),
                            username, password, shard.getDbName()
                    );
                    connectionRequests.add(domainRequest);
                    connectionRequests.add(ipRequest);
                }
            }
            // read user
            if (readUserOptional.isPresent()) {
                final UserDTO readUser = readUserOptional.get();
                final List<ShardInstanceDTO> reads = shard.getReads();
                if (CollectionUtils.isEmpty(reads)) {
                    // read nodes empty, see master info
                    final ShardInstanceDTO master = shard.getMaster();
                    if (null == master) {
                        throw new IllegalStateException(
                                String.format(
                                        "shard中如果存在read权限账号, 必须存在至少一个slave角色的节点信息, 或者存在Master角色的节点信息, clusterName = %s, shardIndex = %s",
                                        cluster.getClusterName(), shard.getShardIndex())
                        );
                    } else {
                        final String username = cipherService.decrypt(readUser.getUsername());
                        final String password = cipherService.decrypt(readUser.getPassword());
                        final ReleaseDatabase databaseRequest = ReleaseDatabase.builder()
                                .ip(master.getIp())
                                .port(master.getPort())
                                .role(Constants.ROLE_SLAVE)
                                .dbName(master.getDbName())
                                .uid(username)
                                .password(password)
                                .readWeight(master.getReadWeight())
                                .build();
                        databaseRequests.add(databaseRequest);
                    }
                } else {
                    final String username = cipherService.decrypt(readUser.getUsername());
                    final String password = cipherService.decrypt(readUser.getPassword());
                    reads.forEach(read -> {
                        final ReleaseDatabase databaseRequest = ReleaseDatabase.builder()
                                .ip(read.getIp())
                                .port(read.getPort())
                                .role(Constants.ROLE_SLAVE)
                                .dbName(read.getDbName())
                                .uid(username)
                                .password(password)
                                .readWeight(read.getReadWeight())
                                .build();
                        databaseRequests.add(databaseRequest);

                        final DBConnectionCheckRequest ipRequest = constructConnectionCheckRequest(
                                read.getIp(), read.getPort(), cluster.getDbCategory(),
                                username, password, shard.getDbName()

                        );
                        connectionRequests.add(ipRequest);
                    });

                    final DBConnectionCheckRequest domainRequest = constructConnectionCheckRequest(
                            shard.getReadDomain(), shard.getReadPort(), cluster.getDbCategory(),
                            username, password, shard.getDbName()

                    );
                    connectionRequests.add(domainRequest);
                }
            }

            // connect check
            final Set<String> enabledReleaseTypes = configService.getDbConnectionCheckEnabledReleaseTypes();
            if (enabledReleaseTypes.contains(releaseType)) {
                connectionRequests.forEach(request -> {
                    final boolean isValid = dbConnectionService.checkConnection(request);
                    if (!isValid) {
                        // invalid
                        throw new IllegalStateException(
                                String.format(
                                        "db connection check result: this database cannot be connected, the release stops, " +
                                                "clusterName = %s, ip = %s, port = %s, dbName = %s",
                                        cluster.getClusterName(), request.getHost(), request.getPort(), request.getDbName()
                                )
                        );
                    }
                });
            }

            // construct shardRequest
            final ReleaseShard shardRequest = ReleaseShard.builder()
                    .index(shard.getShardIndex())
                    .masterDomain(writeUserOptional.isPresent() ? shard.getMasterDomain() : null)
                    .masterPort(writeUserOptional.isPresent() ? shard.getMasterPort() : null)
                    .masterTitanKeys(writeUserOptional.map(UserDTO::getTitanKeys).orElse(null))
                    .slaveDomain(readUserOptional.isPresent() ? shard.getReadDomain() : null)
                    .slavePort(readUserOptional.isPresent() ? shard.getReadPort() : null)
                    .slaveTitanKeys(readUserOptional.map(UserDTO::getTitanKeys).orElse(null))
                    .databases(databaseRequests)
                    .build();
            shardRequests.add(shardRequest);
        });

        // construct extension configs
        String shardStrategies = null;
        String idGenerators = null;
        final List<ClusterExtensionConfig> configs = cluster.getConfigs();
        if (!CollectionUtils.isEmpty(configs)) {
            for (ClusterExtensionConfig config : configs) {
                if (shards_strategies.equals(ClusterExtensionConfigType.getType(config.getType()))) {
                    shardStrategies = config.getContent();
                }

                if (id_generators.equals(ClusterExtensionConfigType.getType(config.getType()))) {
                    idGenerators = config.getContent();
                }
            }
        }

        // construct clusterRequest
        return ReleaseCluster.builder()
                .clusterName(cluster.getClusterName())
                .dbCategory(cluster.getDbCategory())
                .version(cluster.getClusterReleaseVersion() + 1)
                .databaseShards(shardRequests)
                .shardStrategies(shardStrategies)
                .idGenerators(idGenerators)
                .build();
    }

    private DBConnectionCheckRequest constructConnectionCheckRequest(final String host, final Integer port,
                                                                     final String dbCategory, final String username,
                                                                     final String password, final String dbName) {
        return DBConnectionCheckRequest.builder()
                .dbType(dbCategory)
                .env(Constants.ENV)
                .host(host)
                .port(port)
                .user(username)
                .password(password)
                .dbName(dbName)
                .build();
    }


    @DalTransactional(logicDbName = Constants.DATABASE_SET_NAME)
    public void switches(final List<ClusterSwitchesVo> clusterSwitchesVos, final String operator) throws SQLException {

        // Map<clusterName, ClusterDTO>
        final Map<String, ClusterDTO> effectiveClusterDTOMap = Maps.newLinkedHashMapWithExpectedSize(clusterSwitchesVos.size());
        clusterSwitchesVos.forEach(clusterSwitchesVo -> {
            final String clusterName = clusterSwitchesVo.getClusterName();
            try {
                final ClusterDTO effective = findEffectiveClusterDTO(clusterName);

                // invalid cluster
                if (null == effective) {
                    throw new IllegalArgumentException(
                            String.format("集群不存在或已被删除或已被禁用, 本次发布不执行, clusterNames = %s", clusterName)
                    );
                }

                effectiveClusterDTOMap.put(clusterName, effective);
            } catch (SQLException e) {
                throw new RuntimeException("find effective cluster error, please try again later.", e);
            }
        });

        // valid
        switchesValid(effectiveClusterDTOMap, clusterSwitchesVos);

        // batch update
        // Map<clusterName, List<ShardDTO>>
        final Map<String, List<ShardDTO>> effectiveShardDTOsMap = effectiveClusterDTOMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getZones().stream().flatMap(
                                zoneDTO -> zoneDTO.getShards().stream()
                        ).collect(Collectors.toList())
                ));

        final List<Shard> updatedShards = Lists.newArrayList();
        final List<ShardInstance> updatedShardInstances = Lists.newArrayList();
        final List<ShardInstanceDTO> createdShardInstances = Lists.newArrayList();
        // titan key switch target
        final Map<String, Pair<String, Integer>> domainIpPortPair = Maps.newHashMap();

        clusterSwitchesVos.forEach(clusterSwitchesVo -> clusterSwitchesVo.getShards().forEach(shardSwitchesVo -> {
            final ShardDTO shardDTO = effectiveShardDTOsMap.get(clusterSwitchesVo.getClusterName())
                    .stream().filter(
                            effectiveShard -> effectiveShard.getZoneId().equalsIgnoreCase(clusterSwitchesVo.getZoneId())
                                    && effectiveShard.getShardIndex().equals(shardSwitchesVo.getShardIndex())
                    ).collect(Collectors.toList()).get(0);

            final DatabaseSwitchesVo master = shardSwitchesVo.getMaster();
            if (null != master) {
                final InstanceSwitchedVo switchInstanceMaster = master.getInstance();
                final ShardInstanceDTO currentInstance = shardDTO.getMaster();
                if (null == switchInstanceMaster) {
                    final ShardInstance invalided = ShardInstance.builder()
                            .id(currentInstance.getShardInstanceEntityId())
                            .deleted(Deleted.deleted.getCode())
                            .build();
                    updatedShardInstances.add(invalided);
                } else if (switchInstanceMaster.getIp().equalsIgnoreCase(currentInstance.getIp())
                        && switchInstanceMaster.getPort().equals(currentInstance.getPort())) {
                    // ip, port equal
                    final ShardInstance updated = ShardInstance.builder()
                            .id(currentInstance.getShardInstanceEntityId())
                            .readWeight(switchInstanceMaster.getReadWeight())
                            .tags(switchInstanceMaster.getTags())
                            .build();
                    updatedShardInstances.add(updated);
                } else {
                    // ip or port different
                    final ShardInstanceDTO created = ShardInstanceDTO.builder()
                            .shardEntityId(shardDTO.getShardEntityId())
                            .role(Constants.ROLE_MASTER)
                            .readWeight(switchInstanceMaster.getReadWeight())
                            .tags(switchInstanceMaster.getTags())
                            .ip(switchInstanceMaster.getIp())
                            .port(switchInstanceMaster.getPort())
                            .idc("")
                            .build();
                    createdShardInstances.add(created);

                    final ShardInstance invalided = ShardInstance.builder()
                            .id(currentInstance.getShardInstanceEntityId())
                            .deleted(Deleted.deleted.getCode())
                            .build();
                    updatedShardInstances.add(invalided);

                    // titan
//                    final List<UserDTO> effectiveUsers = shardDTO.getUsers();
//                    if (!CollectionUtils.isEmpty(effectiveUsers)) {
//                        effectiveUsers.stream().filter(
//                                userDTO -> Constants.OPERATION_WRITE.equalsIgnoreCase(userDTO.getPermission())
//                        ).map(UserDTO::getTitanKeys).flatMap(
//                                titanKeys -> {
//                                    if (titanKeys.contains(",")) {
//                                        return Lists.newArrayList(titanKeys.split(",")).stream().filter(StringUtils::isNotBlank);
//                                    } else {
//                                        return Lists.newArrayList(titanKeys).stream();
//                                    }
//                                }
//                        ).forEach(titanKey -> {
//                            final TitanKeyMhaUpdateData titanKeyMhaUpdateData = TitanKeyMhaUpdateData.builder()
//                                    .keyName(titanKey)
//                                    .server(switchInstanceMaster.getIp())
//                                    .port(switchInstanceMaster.getPort())
//                                    .build();
//                            titanKeyMhaUpdateDatas.add(titanKeyMhaUpdateData);
//                        });
//                    }
                    domainIpPortPair.put(master.getDomain(), new Pair<>(switchInstanceMaster.getIp(), switchInstanceMaster.getPort()));
                }
            }


            final List<ShardInstanceDTO> currentSlaveInstances = shardDTO.getSlaves();
            final DatabaseSwitchesVo slave = shardSwitchesVo.getSlave();
            if (null != slave) {
                final List<InstanceSwitchedVo> switchInstances = slave.getInstances();
                componentSwitchRead(switchInstances, currentSlaveInstances,
                        shardDTO, Constants.ROLE_SLAVE, updatedShardInstances, createdShardInstances
                );
            }

            final List<ShardInstanceDTO> currentReadInstances = shardDTO.getReads();
            final DatabaseSwitchesVo read = shardSwitchesVo.getRead();
            if (null != read) {
                final List<InstanceSwitchedVo> switchInstances = read.getInstances();
                componentSwitchRead(switchInstances, currentReadInstances,
                        shardDTO, Constants.ROLE_READ, updatedShardInstances, createdShardInstances
                );
            }

            final Shard updatedShard = Shard.builder()
                    .id(shardDTO.getShardEntityId())
                    .masterDomain((null != master && null != master.getDomain()) ? master.getDomain() : null)
                    .masterPort((null != master && null != master.getPort()) ? master.getPort() : null)
                    .slaveDomain((null != slave && null != slave.getDomain()) ? slave.getDomain() : null)
                    .slavePort((null != slave && null != slave.getPort()) ? slave.getPort() : null)
                    .readDomain((null != read && null != read.getDomain()) ? read.getDomain() : null)
                    .readPort((null != read && null != read.getPort() ? read.getPort() : null))
                    .build();
            updatedShards.add(updatedShard);
        }));

        // update shards
        shardService.updateShards(updatedShards);
        // create shardInstance
        instanceService.createInstances(createdShardInstances);
        // invalid shardInstance
        shardInstanceService.updateShardInstances(updatedShardInstances);

        // batch release
        release(
                clusterSwitchesVos.stream().map(ClusterSwitchesVo::getClusterName).collect(Collectors.toList()),
                operator, Constants.RELEASE_TYPE_SWITCH_RELEASE
        );

        // batch switch titan keys
        if (!CollectionUtils.isEmpty(domainIpPortPair)) {
            final List<TitanKeyMhaUpdateData> titanKeyMhaUpdateDatas = Lists.newArrayList();
            final List<String> domains = domainIpPortPair.entrySet().stream()
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            final List<TitanKey> titanKeys = titanKeyService.findByDomains(domains, Enabled.enabled);

            // database titan key miss
//            if (titanKeys.size() != domains.size()) {
//                // TODO: 2019/11/8 cat
//                log.error(
//                        String.format(
//                                "When switching clusters, the titanKeys found in the database is miss, and the target domain is as follows: " +
//                                        "domains = %s, the domains found in the database is as follows : domains = %s, " +
//                                        "maybe the titanKey synchronize schedule is miss data, please pay attention to this case.",
//                                domains.toString(), titanKeys.stream().map(TitanKey::getDomain).collect(Collectors.toList())
//                        )
//                );
//            }

            // construct
            titanKeys.forEach(titanKey -> {
                // each titanKey
                final Pair<String, Integer> ipAndPort = domainIpPortPair.get(titanKey.getDomain());
                final TitanKeyMhaUpdateData titanKeyMhaUpdateData = TitanKeyMhaUpdateData.builder()
                        .keyName(titanKey.getName())
                        .server(ipAndPort.getKey())
                        .port(ipAndPort.getValue())
                        .build();
                titanKeyMhaUpdateDatas.add(titanKeyMhaUpdateData);
            });

            // switch titanKey
            final TitanKeyMhaUpdateRequest request = TitanKeyMhaUpdateRequest.builder()
                    .data(titanKeyMhaUpdateDatas)
                    .env(Constants.ENV)
                    .build();
            final PluginResponse response = titanPluginService.mhaUpdate(request, operator);
            if (response.isSuccess()) {
                log.info(String.format("MhaUpdate Titan Key success, titanKey = %s, ", request.toString()));
            } else {
                throw new DBClusterServiceException(
                        String.format("mhaUpdate titan key error, titanKey = %s, message = %s",
                                request.toString(), response.getMessage())
                );
            }

        }
    }

    private void componentSwitchRead(final List<InstanceSwitchedVo> switchInstances, final List<ShardInstanceDTO> currentInstances,
                                     final ShardDTO shardDTO, final String role,
                                     final List<ShardInstance> updatedShardInstances, final List<ShardInstanceDTO> createdShardInstances) {

        if (CollectionUtils.isEmpty(switchInstances)) {
            currentInstances.forEach(currentInstance -> {
                final ShardInstance invalided = ShardInstance.builder()
                        .id(currentInstance.getShardInstanceEntityId())
                        .deleted(Deleted.deleted.getCode())
                        .build();
                updatedShardInstances.add(invalided);
            });
        } else {
            switchInstances.forEach(switchInstance -> {
                final Optional<ShardInstanceDTO> optional = currentInstances.stream().filter(currentInstance ->
                        currentInstance.getIp().equalsIgnoreCase(switchInstance.getIp())
                                && currentInstance.getPort().equals(switchInstance.getPort())
                ).findFirst();

                if (optional.isPresent()) {
                    // ip, port equal
                    final ShardInstanceDTO currentInstance = optional.get();
                    final ShardInstance updated = ShardInstance.builder()
                            .id(currentInstance.getShardInstanceEntityId())
                            .readWeight(switchInstance.getReadWeight())
                            .tags(switchInstance.getTags())
                            .build();
                    updatedShardInstances.add(updated);
                    currentInstances.remove(currentInstance);
                } else {
                    // ip or port different
                    final ShardInstanceDTO created = ShardInstanceDTO.builder()
                            .shardEntityId(shardDTO.getShardEntityId())
                            .role(role)
                            .readWeight(switchInstance.getReadWeight())
                            .tags(switchInstance.getTags())
                            .ip(switchInstance.getIp())
                            .port(switchInstance.getPort())
                            .idc("")
                            .build();
                    createdShardInstances.add(created);
                }
            });

            currentInstances.forEach(remaining -> {
                final ShardInstance invalided = ShardInstance.builder()
                        .id(remaining.getShardInstanceEntityId())
                        .deleted(Deleted.deleted.getCode())
                        .build();
                updatedShardInstances.add(invalided);
            });
        }
    }

    private void switchesValid(final Map<String, ClusterDTO> effectiveClusterDTOMap,
                               final List<ClusterSwitchesVo> clusterSwitchesVos) {

        // duplicate clusterName
        final long distinctCount = clusterSwitchesVos.stream()
                .map(cluster -> Utils.format(cluster.getClusterName()))
                .distinct().count();
        Preconditions.checkArgument(
                distinctCount == clusterSwitchesVos.size(),
                "clusterName不允许相同, clusterName比较是否相同不区分大小写."
        );

        clusterSwitchesVos.forEach(cluster -> {
            // argument valid
            cluster.valid(regexMatcher);

            final String clusterName = cluster.getClusterName();
            final ClusterDTO effective = effectiveClusterDTOMap.get(clusterName);

            // invalid zoneId
            final String zoneId = cluster.getZoneId();
            final List<ZoneDTO> effectiveZones = effective.getZones();
            if (CollectionUtils.isEmpty(effectiveZones) || !effectiveZones.stream().map(ZoneDTO::getZoneId).collect(Collectors.toList()).contains(zoneId)) {
                throw new IllegalArgumentException(
                        String.format("集群下的zone不存在或已被删除或已被禁用, 本次发布不执行, clusterNames = %s, zoneId = %s", clusterName, zoneId)
                );
            }

            // shard
            final List<ShardDTO> effectiveShardDTOs = effectiveZones.stream().filter(
                    zoneDTO -> zoneId.equalsIgnoreCase(zoneDTO.getZoneId())
            ).collect(Collectors.toList()).get(0).getShards();
            if (CollectionUtils.isEmpty(effectiveShardDTOs)) {
                throw new IllegalArgumentException(
                        String.format("clusterNames = %s, zoneId = %s 下所有的shard均不存在或已被删除或已被禁用, 本次发布不执行.", clusterName, zoneId)
                );
            } else {
                final List<Integer> effectiveShardIndexes = effectiveShardDTOs.stream()
                        .map(ShardDTO::getShardIndex).collect(Collectors.toList());

                final List<ShardSwitchesVo> shardSwitchesVos = cluster.getShards();
                // empty shards
                if (CollectionUtils.isEmpty(shardSwitchesVos)) {
                    throw new IllegalArgumentException(
                            String.format("clusterNames = %s, zoneId = %s 下shard为空, 本次发布不执行.", clusterName, zoneId)
                    );
                }

                // invalid shards
                shardSwitchesVos.forEach(shardSwitchesVo -> {
                    final Integer switchShardIndex = shardSwitchesVo.getShardIndex();
                    if (!effectiveShardIndexes.contains(switchShardIndex)) {
                        throw new IllegalArgumentException(
                                String.format(
                                        "参数中含有不存在或已被删除或已被禁用的shard, 本次发布不执行, clusterName = %s, zoneId = %s, shardIndex = %s.",
                                        clusterName, zoneId, switchShardIndex
                                )
                        );
                    }
                });
            }

            // argument correct
            cluster.correct();
        });
    }


    // deprecated
    public Integer addAndGetId(final ClusterVo clusterVo) throws SQLException {
        Cluster cluster = Cluster.builder()
                .clusterName(clusterVo.getClusterName())
                .dbCategory(clusterVo.getDbCategory())
                .enabled(Enabled.enabled.getCode())
                .deleted(Deleted.un_deleted.getCode())
                .releaseVersion(0)
                .build();

        KeyHolder keyHolder = new KeyHolder();
        clusterDao.insertWithKeyHolder(keyHolder, cluster);
        return keyHolder.getKey().intValue();
    }

    public Cluster findById(Integer clusterId) throws SQLException {
        return clusterDao.queryByPk(clusterId);
    }

    public List<Cluster> findByIds(List<Integer> clusterIds) throws SQLException {
        List<Cluster> clusters = Lists.newArrayList();
        for (Integer clusterId : clusterIds) {
            Cluster cluster = findById(clusterId);
            clusters.add(cluster);
        }
        return clusters;
    }

    public List<Cluster> findByClusterNames(List<String> clusterNames) throws SQLException {
        return clusterDao.findByClusterNames(clusterNames);
    }

    public int update(Cluster cluster) throws SQLException {
        return clusterDao.update(null, cluster);
    }

    public int[] update(List<Cluster> clusters) throws SQLException {
        return clusterDao.update(null, clusters);
    }

    public int[] updateReleaseInfo(List<String> clusterNames) throws SQLException {
        List<Cluster> clusters = findByClusterNames(clusterNames);

        for (Cluster cluster : clusters) {
            // increase version
            int version = cluster.getReleaseVersion() + 1;
            cluster.setReleaseVersion(version);
            cluster.setReleaseTime(new Timestamp(System.currentTimeMillis()));
        }

        return update(clusters);
    }
}
