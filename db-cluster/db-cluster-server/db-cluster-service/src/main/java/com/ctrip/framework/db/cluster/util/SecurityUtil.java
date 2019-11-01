package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.ctrip.framework.db.cluster.vo.dal.create.UserVo;

import java.util.List;

/**
 * Created by shenjie on 2019/8/14.
 */
public class SecurityUtil {

    public static void encryptPassword(ClusterVo cluster) {
        List<ShardVo> shards = cluster.deprGetShards();
        if (shards != null && !shards.isEmpty()) {
            for (ShardVo shard : shards) {
                List<UserVo> users = shard.deprGetUsers();
                if (users != null && !users.isEmpty()) {
                    for (UserVo user : users) {
                        String decryptedPassword = RC4.encrypt(user.getPassword(), null);
                        user.setPassword(decryptedPassword);
                    }
                }
            }
        }
    }
}
