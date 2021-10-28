package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

/**
 *
 */
public class DateParamContext extends ParamContext {

	private static final long serialVersionUID = -135291105713694295L;

	/**
	 * @param index
	 * @param values
	 */
	public DateParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1) {
			stmt.setDate(index, (Date) values[0]);
		} else if (values.length == 2) {
			stmt.setDate(index, (Date) values[0], (Calendar) values[1]);
		}
	}

}
