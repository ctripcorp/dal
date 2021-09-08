/**
 * Project: zebra-client
 *
 * File Created at 2011-6-19
 * $Id$
 *
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.ctrip.platform.dal.dao.datasource.read.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 */
public class TimestampParamContext extends ParamContext {

	private static final long serialVersionUID = 6195002993364551047L;

	/**
	 * @param index
	 * @param values
	 */
	public TimestampParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1) {
			stmt.setTimestamp(index, (Timestamp) values[0]);
		} else if (values.length == 2) {
			stmt.setTimestamp(index, (Timestamp) values[0], (Calendar) values[1]);
		}
	}

}
