package com.ctrip.platform.dao.sqldao;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.common.AbstractDAO;
import com.ctrip.platform.dao.exception.ParametersInvalidException;
import com.ctrip.platform.dao.msg.AvailableType;

public class FreeSQLPersonDAO extends AbstractDAO{
	
	private static final Logger logger = LoggerFactory
			.getLogger(FreeSQLPersonDAO.class);

	public FreeSQLPersonDAO() {
	}

	/**
	 * Query Address Telephone according to the name and gender
	 * 
	 * @return The DAO function object to validate the parameter
	 */
	public ResultSet getAddrAndTel(AvailableType... params)
			throws Exception {

		final String sql = "SELECT Address, Telephone FROM Person WHERE Name = ? AND Gender IN (?)";


		return super.fetch(null, sql, 0, params);
	}

}
