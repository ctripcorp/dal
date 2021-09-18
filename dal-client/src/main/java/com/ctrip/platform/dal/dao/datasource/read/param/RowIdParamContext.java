package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;

/**
 *
 */
public class RowIdParamContext extends ParamContext {

	private static final long serialVersionUID = 7121140907131100800L;

	/**
	 * @param index
	 * @param values
	 */
	public RowIdParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setRowId(index, (RowId) values[0]);
	}

}
