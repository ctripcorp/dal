package com.ctrip.platform.dal.dao.datasource.read.param;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class BigDecimalParamContext extends ParamContext {

	private static final long serialVersionUID = -6915832597431575810L;

	/**
	 * @param index
	 * @param values
	 */
	public BigDecimalParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setBigDecimal(index, (BigDecimal) values[0]);
	}

}
