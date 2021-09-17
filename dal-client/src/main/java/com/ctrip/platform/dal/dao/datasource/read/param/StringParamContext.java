package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class StringParamContext extends ParamContext {

	private static final long serialVersionUID = -4522806074499008890L;

	/**
	 * @param index
	 * @param values
	 */
	public StringParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setString(index, (String) values[0]);
	}
}
