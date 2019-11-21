package cluster;

import com.ctrip.platform.dal.dao.DalHints;
import dao.CommonDao;
import entity.StrategyMeta;

import java.sql.SQLException;

public class FltShardService {

    private volatile CommonDao<StrategyMeta> dao;

    public FltShardService() {}

    public Long getShardValue(Long rawValue) throws SQLException {
        StrategyMeta meta = getDao().queryByPk(rawValue, new DalHints());
        return meta != null ? (long) meta.getSid() : null;
    }

    private CommonDao<StrategyMeta> getDao() throws SQLException {
        if (dao == null)
            synchronized (this) {
                if (dao == null)
                    dao = new CommonDao<>(StrategyMeta.class);
            }
        return dao;
    }

}
