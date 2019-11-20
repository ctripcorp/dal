package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.TitanKeyDao;
import com.ctrip.framework.db.cluster.entity.TitanKey;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.vo.dal.create.TitanKeyVo;
import com.ctrip.platform.dal.dao.DalHints;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.ctrip.platform.dal.dao.DalHintEnum.updateNullField;

/**
 * Created by shenjie on 2019/3/22.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TitanKeyService {

    private final TitanKeyDao titanKeyDao;


    public void create(List<TitanKey> titanKeys) throws SQLException {
        titanKeyDao.batchInsert(titanKeys);
    }

    public void update(List<TitanKey> titanKeys) throws SQLException {
        titanKeyDao.update(new DalHints(updateNullField), titanKeys);
    }

    public List<TitanKey> findByKeyNames(final List<String> keyNames) throws SQLException {
        return titanKeyDao.queryByNames(keyNames);
    }

    public List<TitanKey> findByDomains(final List<String> domains, final Enabled enabled) throws SQLException {
        return titanKeyDao.queryByDomains(domains, enabled);
    }

    public List<TitanKey> findKeyNameAndSubEnv() throws SQLException {
        return titanKeyDao.findKeyNameAndSubEnv();
    }

    // deprecated
    public void add(List<TitanKeyVo> titanKeys, Map<String, Integer> userIds) throws SQLException {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        for (TitanKeyVo titanKeyVo : titanKeys) {
            Integer userId = userIds.get(titanKeyVo.getUid());
            TitanKey titanKey = TitanKey.builder()
                    .name(titanKeyVo.getKeyName())
//                    .userId(userId)
//                    .extParam(titanKeyVo.getExtParam())
//                    .timeout(titanKeyVo.getTimeOut())
//                    .status(Utils.getStatusCode(titanKeyVo.getEnabled()))
                    .createUser(titanKeyVo.getCreateUser())
                    .updateUser(titanKeyVo.getUpdateUser())
                    .permissions(titanKeyVo.getPermissions())
                    .freeVerifyIps(titanKeyVo.getFreeVerifyIpList())
                    .freeVerifyApps(titanKeyVo.getFreeVerifyAppIdList())
                    .build();
            titanKeyDao.insert(null, titanKey);
        }
    }

    public List<TitanKey> findTitanKeysByUserId(Integer userId) throws SQLException {
        TitanKey titanKey = TitanKey.builder()
//                .userId(userId)
                .build();
        List<TitanKey> titanKeys = titanKeyDao.queryBy(titanKey);
        return titanKeys;
    }
}
