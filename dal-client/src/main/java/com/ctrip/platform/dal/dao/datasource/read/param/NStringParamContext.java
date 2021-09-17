package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class NStringParamContext extends ParamContext {

	private static final long serialVersionUID = 4189680077757800337L;

	/**
	 * @param index
	 * @param values
	 */
	public NStringParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setNString(index, (String) values[0]);
	}

}
