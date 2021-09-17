package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class ByteParamContext extends ParamContext {

	private static final long serialVersionUID = -9109030072336300120L;

	/**
	 * @param index
	 * @param values
	 */
	public ByteParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		stmt.setByte(index, (Byte) values[0]);
	}

}
