package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class DoubleParamContext extends ParamContext {

	private static final long serialVersionUID = -8295120306414575222L;

	/**
	 * @param index
	 * @param values
	 */
	public DoubleParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setDouble(index, (Double) values[0]);
	}

}
