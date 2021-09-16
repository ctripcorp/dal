package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class IntParamContext extends ParamContext {

	private static final long serialVersionUID = -4094567388337032659L;

	/**
	 * @param index
	 * @param values
	 */
	public IntParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setInt(index, (Integer) values[0]);
	}

}
