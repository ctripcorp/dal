package com.ctrip.framework.db.cluster.vo.dal.create;

import com.ctrip.framework.db.cluster.domain.dto.ShardDTO;
import com.ctrip.framework.db.cluster.domain.dto.ShardInstanceDTO;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by shenjie on 2019/3/7.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShardVo {

    private Integer shardIndex;

    private String dbName;

    private DatabaseVo master;

    private DatabaseVo slave;

    private DatabaseVo read;

    private List<UserVo> users;


    public void valid(final RegexMatcher regexMatcher) {
        // shardIndex
        Preconditions.checkNotNull(shardIndex, "shardIndex不允许为空.");

        // dbName
        Preconditions.checkArgument(StringUtils.isNotBlank(dbName), "dbName不允许为空.");
        Preconditions.checkArgument(regexMatcher.dbName(dbName), "dbName不合法.");

        // master
        if (null != master) {
            Preconditions.checkArgument(CollectionUtils.isEmpty(master.getInstances()), "master instances字段不能赋值.");
            master.valid(regexMatcher);
        }

        // slave
        if (null != slave) {
            Preconditions.checkArgument(null == slave.getInstance(), "slave instance字段不能赋值.");
            slave.valid(regexMatcher);
        }

        // read
        if (null != read) {
            Preconditions.checkArgument(null == read.getInstance(), "read instance字段不能赋值.");
            read.valid(regexMatcher);
        }
    }

    public void correct() {
        // lower case
        this.dbName = Utils.format(this.dbName);

        // db correct
        if (null != master) {
            master.correct();
        }

        if (null != slave) {
            slave.correct();
        }

        if (null != read) {
            read.correct();
        }
    }

    public ShardDTO toDTO() {
        ShardInstanceDTO masterShardInstanceDTO = null;
        // construct master
        if (null != master && null != master.getInstance()) {
            final InstanceVo masterInstance = master.getInstance();
            masterShardInstanceDTO = ShardInstanceDTO.builder()
                    .role(Constants.ROLE_MASTER)
                    .readWeight(masterInstance.getReadWeight())
                    .tags(masterInstance.getTags())
                    .ip(masterInstance.getIp())
                    .port(masterInstance.getPort())
                    .build();
        }

        // construct slaves
        final List<ShardInstanceDTO> slaveShardInstanceDTOs = Lists.newArrayList();
        if (null != slave && !CollectionUtils.isEmpty(slave.getInstances())) {
            slave.getInstances().forEach(slaveInstance -> {
                final ShardInstanceDTO slaveShardInstanceDTO = ShardInstanceDTO.builder()
                        .role(Constants.ROLE_SLAVE)
                        .readWeight(slaveInstance.getReadWeight())
                        .tags(slaveInstance.getTags())
                        .ip(slaveInstance.getIp())
                        .port(slaveInstance.getPort())
                        .build();
                slaveShardInstanceDTOs.add(slaveShardInstanceDTO);
            });
        }

        // construct reads
        final List<ShardInstanceDTO> readShardInstanceDTOs = Lists.newArrayList();
        if (null != read && !CollectionUtils.isEmpty(read.getInstances())) {
            read.getInstances().forEach(readInstance -> {
                final ShardInstanceDTO readShardInstanceDTO = ShardInstanceDTO.builder()
                        .role(Constants.ROLE_READ)
                        .readWeight(readInstance.getReadWeight())
                        .tags(readInstance.getTags())
                        .ip(readInstance.getIp())
                        .port(readInstance.getPort())
                        .build();
                readShardInstanceDTOs.add(readShardInstanceDTO);
            });
        }

        return ShardDTO.builder()
                .shardIndex(this.shardIndex)
                .dbName(this.dbName)
                .masterDomain(null == master ? null : master.getDomain())
                .masterPort(null == master ? null : master.getPort())
                .slaveDomain(null == slave ? null : slave.getDomain())
                .slavePort(null == slave ? null : slave.getPort())
                .readDomain(null == read ? null : read.getDomain())
                .readPort(null == read ? null : read.getPort())
                .master(masterShardInstanceDTO)
                .slaves(slaveShardInstanceDTOs)
                .reads(readShardInstanceDTOs)
                .build();
    }
}
