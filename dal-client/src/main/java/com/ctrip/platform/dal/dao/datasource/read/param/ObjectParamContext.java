package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class ObjectParamContext extends ParamContext {

	private static final long serialVersionUID = -8970760913439129412L;

	/**
	 * @param index
	 * @param values
	 */
	public ObjectParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1) {
			stmt.setObject(index, values[0]);
		} else if (values.length == 2) {
			stmt.setObject(index, values[0], (Integer) values[1]);
		} else if (values.length == 3) {
			stmt.setObject(index, values[0], (Integer) values[1], (Integer) values[2]);
		}
	}

}
