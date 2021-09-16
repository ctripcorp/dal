package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class ShortParamContext extends ParamContext {

	private static final long serialVersionUID = 99912191693491650L;

	/**
	 * @param index
	 * @param values
	 */
	public ShortParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setShort(index, (Short) values[0]);
	}

}
