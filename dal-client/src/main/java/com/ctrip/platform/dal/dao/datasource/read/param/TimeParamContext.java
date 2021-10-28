package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

/**
 *
 */
public class TimeParamContext extends ParamContext {

	private static final long serialVersionUID = -8378628871695126103L;

	/**
	 * @param index
	 * @param values
	 */
	public TimeParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1) {
			stmt.setTime(index, (Time) values[0]);
		} else if (values.length == 2) {
			stmt.setTime(index, (Time) values[0], (Calendar) values[1]);
		}
	}

}
