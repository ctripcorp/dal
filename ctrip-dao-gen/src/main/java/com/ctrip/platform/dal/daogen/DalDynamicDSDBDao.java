package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.daogen.dao.BaseDao;
import com.ctrip.platform.dal.daogen.entity.TitanKeySwitchInfoDB;
import com.dianping.cat.Cat;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by taochen on 2019/7/19.
 */
public class DalDynamicDSDBDao extends BaseDao {
    private static DalDynamicDSDBDao dalDynamicDSDBDao = null;

    private DalTableDao<TitanKeySwitchInfoDB> client;

    private DalRowMapper<TitanKeySwitchInfoDB> dalRowMapper = null;

    private DalDynamicDSDBDao() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(TitanKeySwitchInfoDB.class));
        dalRowMapper = new DalDefaultJpaMapper<>(TitanKeySwitchInfoDB.class);
    }

    public static DalDynamicDSDBDao getInstance() {
        if (dalDynamicDSDBDao == null) {
            try {
                dalDynamicDSDBDao = new DalDynamicDSDBDao();
            } catch (SQLException e) {
                Cat.logError("init DalDynamicDSDBDao fail!", e);
            }
        }
        return dalDynamicDSDBDao;
    }

    public void batchInsertSwitchData(List<TitanKeySwitchInfoDB> switchInfoList) {
        if (switchInfoList == null || switchInfoList.size() == 0) {
             return;
        }
        try {
            client.batchInsert(new DalHints(), switchInfoList);
        } catch (SQLException e) {
            Cat.logError("insert switch data [" + switchInfoList.get(0).getCheckTime() + "] fail!", e);
        }
    }

    public List<TitanKeySwitchInfoDB> queryInRange(String startCheckTime, String endCheckTime) {
        List<TitanKeySwitchInfoDB> switchInfoDBList = null;
        if (StringUtils.isEmpty(startCheckTime) || StringUtils.isEmpty(endCheckTime)) {
            return null;
        }
        String whereClause = "checkTime >= %s and checkTime <= %s";
        try {
            switchInfoDBList = client.query(String.format(whereClause, startCheckTime, endCheckTime), new StatementParameters(), new DalHints());
        } catch (SQLException e) {
            Cat.logError("query " + startCheckTime + ", " + endCheckTime + " range data fail!", e);
        }
        return switchInfoDBList;
    }

}
