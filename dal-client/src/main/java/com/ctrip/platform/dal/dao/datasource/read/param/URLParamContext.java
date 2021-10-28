package com.ctrip.platform.dal.dao.datasource.read.param;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class URLParamContext extends ParamContext {

	private static final long serialVersionUID = -4146564212387933157L;

	/**
	 * @param index
	 * @param values
	 */
	public URLParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setURL(index, (URL) values[0]);
	}

}
