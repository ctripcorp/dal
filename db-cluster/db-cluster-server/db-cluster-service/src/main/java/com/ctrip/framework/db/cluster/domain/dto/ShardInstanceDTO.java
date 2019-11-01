package com.ctrip.framework.db.cluster.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Created by @author zhuYongMing on 2019/10/26.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShardInstanceDTO {

    // shardInstance info
    private Integer shardInstanceEntityId;

    private Integer shardEntityId;

    private Integer instanceEntityId;

    private String role;

    private Integer readWeight;

    private String tags;

    private Integer shardInstanceDeleted;

    private Integer shardInstanceMemberStatus;

    private Integer shardInstanceHealthStatus;

    private Timestamp shardInstanceCreateTime;

    private Timestamp shardInstanceUpdateTime;


    // instance info
    private String ip;

    private Integer port;

    private String idc;

    private Integer instanceDeleted;

    private Timestamp instanceCreateTime;

    private Timestamp instanceUpdateTime;


    // Redundant info
    private Integer shardIndex;

    private String dbName;

//    private Integer zoneEntityId;
//
//    private String zoneId;
//
//    private Integer clusterEntityId;
//
//    private String clusterName;
}
