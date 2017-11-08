package test.com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.EntityManager;

/**
 * Just a sample
 * @author jhhe
 *
 */
public class CommonDao {
    public <T> int insert(T pojo, DalHints hints) throws SQLException {
        DalTableDao<T> dao = (DalTableDao<T>) new DalTableDao<>(pojo.getClass());
        return dao.insert(hints, pojo);
    }
    
    public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) throws SQLException {
        DalQueryDao dao = new DalQueryDao(EntityManager.getEntityManager(clazz).getDatabaseName());
        return dao.query(sql, parameters, hints, clazz);
    }
}
