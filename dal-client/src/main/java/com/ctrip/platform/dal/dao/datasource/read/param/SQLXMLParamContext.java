package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;

/**
 *
 */
public class SQLXMLParamContext extends ParamContext {

	private static final long serialVersionUID = -3225485567882941489L;

	/**
	 * @param index
	 * @param values
	 */
	public SQLXMLParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setSQLXML(index, (SQLXML) values[0]);
	}

}
