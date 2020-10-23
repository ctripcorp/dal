package com.ctrip.platform.dal.application.dao;

import com.ctrip.platform.dal.application.entity.OrderConfig;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

import java.sql.SQLException;

public class OrderConfigDao {
    private static final boolean ASC = true;
    private DalTableDao<OrderConfig> client;

    public OrderConfigDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(OrderConfig.class));
    }
    /**
     * Query OrderConfig by OrderConfig instance which the primary key is set
     */
    public OrderConfig queryByPk(OrderConfig pk)
            throws SQLException {
        return queryByPk(pk, null);
    }

    /**
     * Query OrderConfig by OrderConfig instance which the primary key is set
     */
    public OrderConfig queryByPk(OrderConfig pk, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryByPk(pk, hints);
    }
    /**
     * Insert single pojo
     *
     * @param daoPojo
     *            pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(OrderConfig daoPojo) throws SQLException {
        return insert(null, daoPojo);
    }

    /**
     * Insert single pojo
     *
     * @param hints
     *            Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojo
     *            pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(DalHints hints, OrderConfig daoPojo) throws SQLException {
        if (null == daoPojo) {
            return 0;
        }
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, daoPojo);
    }
}
