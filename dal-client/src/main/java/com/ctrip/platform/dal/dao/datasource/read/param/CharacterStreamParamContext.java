package com.ctrip.platform.dal.dao.datasource.read.param;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class CharacterStreamParamContext extends ParamContext {

	private static final long serialVersionUID = -709829341915993025L;

	/**
	 * @param index
	 * @param values
	 */
	public CharacterStreamParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1) {
			stmt.setCharacterStream(index, (Reader) values[0]);
		} else if (values.length == 2 && values[1] instanceof Integer) {
			stmt.setCharacterStream(index, (Reader) values[0], (Integer) values[1]);
		} else if (values.length == 2 && values[1] instanceof Long) {
			stmt.setCharacterStream(index, (Reader) values[0], (Long) values[1]);
		}
	}

}
