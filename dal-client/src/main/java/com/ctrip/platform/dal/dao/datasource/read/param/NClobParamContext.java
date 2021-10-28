package com.ctrip.platform.dal.dao.datasource.read.param;

import java.io.Reader;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class NClobParamContext extends ParamContext {

	private static final long serialVersionUID = 4265092847567327751L;

	/**
	 * @param index
	 * @param values
	 */
	public NClobParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1 && values[0] instanceof NClob) {
			stmt.setNClob(index, (NClob) values[0]);
		} else if (values.length == 1 && values[0] instanceof Reader) {
			stmt.setNClob(index, (Reader) values[0]);
		} else if (values.length == 2) {
			stmt.setNClob(index, (Reader) values[0], (Long) values[1]);
		}

	}

}
