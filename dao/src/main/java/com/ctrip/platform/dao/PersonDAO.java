package com.ctrip.platform.dao;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.common.AbstractDAO;
import com.ctrip.platform.dao.exception.ParametersInvalidException;
import com.ctrip.platform.dao.param.Parameter;

public class PersonDAO extends AbstractDAO {

	private static final Logger logger = LoggerFactory
			.getLogger(PersonDAO.class);

	public PersonDAO() {
	}

	/**
	 * Query Address Telephone according to the name and gender
	 * 
	 * @return The DAO function object to validate the parameter
	 */
	public ResultSet getAddrAndTel(Parameter... params)
			throws Exception {

		final int paramCount = 2;

		final String sql = "SELECT Address, Telephone FROM Person WHERE Name = ? AND Gender = ?";

		if (params.length != paramCount) {
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", paramCount,
					params.length));
		}

		return super.fetch(null, sql, 0, params);
	}

	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int SetAddrByName(Parameter... params) throws Exception {

		final int paramCount = 2;

		final String sql = "UPDATE Person SET Address = ? WHERE Name = ?";

		if (params.length != paramCount) {
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", paramCount,
					params.length));
		}

		return super.execute(null, sql, 0, params);
	}

	

}
