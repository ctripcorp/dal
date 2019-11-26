package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.TitanKeyDao;
import com.ctrip.framework.db.cluster.entity.TitanKey;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.platform.dal.dao.DalHints;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

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

    public List<TitanKey> queryByNamesAndSubEnv(final List<String> keyNames, final String subEnv) throws SQLException {
        return titanKeyDao.queryByNamesAndSubEnv(keyNames, subEnv);
    }

    public List<TitanKey> findByDomains(final List<String> domains, final Enabled enabled) throws SQLException {
        return titanKeyDao.queryByDomains(domains, enabled);
    }

    public List<TitanKey> findKeyNameAndSubEnv() throws SQLException {
        return titanKeyDao.findKeyNameAndSubEnv();
    }
}
