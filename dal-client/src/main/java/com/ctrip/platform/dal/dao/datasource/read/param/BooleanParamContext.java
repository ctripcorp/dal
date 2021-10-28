package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class BooleanParamContext extends ParamContext {

	private static final long serialVersionUID = -4390286596448078383L;

	/**
	 * @param index
	 * @param values
	 */
	public BooleanParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setBoolean(index, (Boolean) values[0]);
	}

}
