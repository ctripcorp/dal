package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class NullParamContext extends ParamContext {

	private static final long serialVersionUID = 7259410895206430159L;

	/**
	 * @param index
	 * @param values
	 */
	public NullParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 *
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1) {
			stmt.setNull(index, (Integer) values[0]);
		} else if (values.length == 2) {
			stmt.setNull(index, (Integer) values[0], (String) values[1]);
		}
	}

}