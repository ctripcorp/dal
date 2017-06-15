package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;

public interface CustomizableMapper<T> {
    /**
     * To customize columns to be mapped for table selection. This will be invoked before
     * DalRowMapper<T> mapWith(ResultSet rs, DalHints hints)
     * 
     * @param columns
     * @return a new instance of mapper
     * @throws SQLException
     */
    DalRowMapper<T> mapWith(String[] columns) throws SQLException;
    
    /**
     * To customize behavior against result set meta data and hints. Used for both table and free selection.
     * 
     * @param rs
     * @param hints
     * @return
     * @throws SQLException
     */
	DalRowMapper<T> mapWith(ResultSet rs, DalHints hints) throws SQLException;
}
