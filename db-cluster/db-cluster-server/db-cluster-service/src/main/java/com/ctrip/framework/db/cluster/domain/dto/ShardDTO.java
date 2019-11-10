package com.ctrip.framework.db.cluster.domain.dto;

import com.ctrip.framework.db.cluster.enums.ShardInstanceHealthStatus;
import com.ctrip.framework.db.cluster.enums.ShardInstanceMemberStatus;
import com.ctrip.framework.db.cluster.vo.dal.create.DatabaseVo;
import com.ctrip.framework.db.cluster.vo.dal.create.InstanceVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/10/26.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShardDTO {

    // shard info
    private Integer shardEntityId;

    private Integer shardIndex;

    private Integer clusterEntityId;

    private String zoneId;

    private String dbName;

    private String masterDomain;

    private Integer masterPort;

    private String slaveDomain;

    private Integer slavePort;

    private String readDomain;

    private Integer readPort;

    private Integer shardDeleted;

    private Timestamp shardCreateTime;

    private Timestamp shardUpdateTime;


    // Cascade info
    private ShardInstanceDTO master;

    private List<ShardInstanceDTO> slaves;

    private List<ShardInstanceDTO> reads;

    private List<UserDTO> users;


    // Redundant info
//    private Integer zoneEntityId;
//
//    private String clusterName;


    public ShardVo toVo() {
        DatabaseVo masterDatabaseVo = null;
        if (master != null) {
            final InstanceVo instanceVo = InstanceVo.builder()
                    .ip(master.getIp())
                    .port(master.getPort())
                    .readWeight(master.getReadWeight())
                    .tags(master.getTags())
                    .memberStatus(ShardInstanceMemberStatus.getShardInstanceMemberStatus(master.getShardInstanceMemberStatus()).convertToBoolean())
                    .healthStatus(ShardInstanceHealthStatus.getShardInstanceHealthStatus(master.getShardInstanceHealthStatus()).convertToBoolean())
                    .build();

            masterDatabaseVo = DatabaseVo.builder()
                    .domain(masterDomain)
                    .port(masterPort)
                    .instance(instanceVo)
                    .build();
        }

        DatabaseVo slaveDatabaseVo = null;
        if (!CollectionUtils.isEmpty(slaves)) {
            final List<InstanceVo> instanceVos = slaves.stream().map(
                    slave -> InstanceVo.builder()
                            .ip(slave.getIp())
                            .port(slave.getPort())
                            .readWeight(slave.getReadWeight())
                            .tags(slave.getTags())
                            .memberStatus(ShardInstanceMemberStatus.getShardInstanceMemberStatus(slave.getShardInstanceMemberStatus()).convertToBoolean())
                            .healthStatus(ShardInstanceHealthStatus.getShardInstanceHealthStatus(slave.getShardInstanceHealthStatus()).convertToBoolean())
                            .build()
            ).collect(Collectors.toList());

            slaveDatabaseVo = DatabaseVo.builder()
                    .domain(slaveDomain)
                    .port(slavePort)
                    .instances(instanceVos)
                    .build();
        }

        DatabaseVo readDatabaseVo = null;
        if (!CollectionUtils.isEmpty(reads)) {
            final List<InstanceVo> instanceVos = reads.stream().map(
                    read -> InstanceVo.builder()
                            .ip(read.getIp())
                            .port(read.getPort())
                            .readWeight(read.getReadWeight())
                            .tags(read.getTags())
                            .memberStatus(ShardInstanceMemberStatus.getShardInstanceMemberStatus(read.getShardInstanceMemberStatus()).convertToBoolean())
                            .healthStatus(ShardInstanceHealthStatus.getShardInstanceHealthStatus(read.getShardInstanceHealthStatus()).convertToBoolean())
                            .build()
            ).collect(Collectors.toList());

            readDatabaseVo = DatabaseVo.builder()
                    .domain(readDomain)
                    .port(readPort)
                    .instances(instanceVos)
                    .build();
        }

        return ShardVo.builder()
                .shardIndex(shardIndex)
                .dbName(dbName)
                .master(masterDatabaseVo)
                .slave(slaveDatabaseVo)
                .read(readDatabaseVo)
                .build();
    }
}
