package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class ByteArrayParamContext extends ParamContext {

	private static final long serialVersionUID = -4010750314366977703L;

	/**
	 * @param index
	 * @param values
	 */
	public ByteArrayParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setBytes(index, (byte[]) values[0]);
	}

}
