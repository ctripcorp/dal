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

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class BinaryStreamParamContext extends ParamContext {

	private static final long serialVersionUID = 8395319863452179327L;

	/**
	 * @param index
	 * @param values
	 */
	public BinaryStreamParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1) {
			stmt.setBinaryStream(index, (InputStream) values[0]);
		} else if (values.length == 2 && values[1] instanceof Integer) {
			stmt.setBinaryStream(index, (InputStream) values[0], (Integer) values[1]);
		} else if (values.length == 2 && values[1] instanceof Long) {
			stmt.setBinaryStream(index, (InputStream) values[0], (Long) values[1]);
		}
	}

}
