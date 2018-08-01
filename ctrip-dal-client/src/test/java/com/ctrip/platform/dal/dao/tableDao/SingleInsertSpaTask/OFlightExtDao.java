package com.ctrip.platform.dal.dao.tableDao.SingleInsertSpaTask;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

import java.sql.SQLException;

public class OFlightExtDao {
    private static final boolean ASC = true;
    private DalTableDao<OFlightExt> client;

    public OFlightExtDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(OFlightExt.class));
    }

    /**
     * Insert single pojo
     *
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(OFlightExt daoPojo) throws SQLException {
        return insert(null, daoPojo);
    }

    /**
     * Insert single pojo
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(DalHints hints, OFlightExt daoPojo) throws SQLException {
        if (null == daoPojo) {
            return 0;
        }
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, daoPojo);
    }

    /**
     * Insert pojo and get the generated PK back in keyHolder. If the "set no count on" for MS SqlServer is set, the
     * operation may fail. Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param keyHolder holder for generated primary keys
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(DalHints hints, KeyHolder keyHolder, OFlightExt daoPojo) throws SQLException {
        if (null == daoPojo) {
            return 0;
        }
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, keyHolder, daoPojo);
    }

}
