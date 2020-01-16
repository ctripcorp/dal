package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by taochen on 2019/10/15.
 */
public class MyDalResultSetExtractor implements DalResultSetExtractor {
    @Override
    public Object extract(ResultSet resultSet) throws SQLException {
        return null;
    }
}
