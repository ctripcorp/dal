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
public class UserDTO {

    private Integer userEntityId;

    private Integer clusterEntityId;

    private Integer shardIndex;

    private String username;

    private String password;

    private String permission;

    private String tag;

    private String titanKeys;

    private Integer userEnabled;

    private Integer userDeleted;

    private Timestamp userCreateTime;

    private Timestamp userUpdateTime;


    // Redundant info
//    private Integer shardEntityId;
//
//    private Integer zoneEntityId;
//
//    private String zoneId;
//
//    private String clusterName;
}
