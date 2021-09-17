package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class FloatParamContext extends ParamContext {

	private static final long serialVersionUID = -8854468770215209575L;

	/**
	 * @param index
	 * @param values
	 */
	public FloatParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setFloat(index, (Float) values[0]);
	}

}
