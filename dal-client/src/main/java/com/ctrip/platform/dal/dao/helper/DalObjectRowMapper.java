package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalRowMapper;

/**
 * In case you need get Short cloumn value from Db, use ShortRowMapper 
 * instead of this, because the default getObject will return Interger instead of Short for such column.
 * 
 * @author jhhe
 *
 * @param <T>
 */
public class DalObjectRowMapper<T> implements DalRowMapper<T> {

	@SuppressWarnings("unchecked")
	public T map(ResultSet rs, int rowNum) throws SQLException {
		return (T)rs.getObject(1);
	}
}