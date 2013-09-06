package com.ctrip.platform.dao;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.common.AbstractDAO;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class GeneratorDAOSPDAO extends AbstractDAO {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GeneratorDAOSPDAO.class);

	public ResultSet fetchBySp(String ctnCtxt, String sp, int flag,
			Parameter... params) throws Exception {

		return super.fetchBySp(null, sp, 0, params);

	}

	public int executeSp(String tnxCtxt, String sp, int flag,
			Parameter... params) throws Exception {

		return super.executeSp(tnxCtxt, sp, flag, params);

	}

	public static void main(String[] args) throws Exception {

		// AvailableType inputParam = new <Integer> AvailableType(1, 1);

		GeneratorDAOSPDAO spDAO = new GeneratorDAOSPDAO();

		spDAO.setUseDBClient(true);

		Parameter nameParam = ParameterFactory.createStringParameter(1, "gawu");
		Parameter addrParam = ParameterFactory.createStringParameter(2,
				"shanghai");

		int row = spDAO
				.executeSp(null, "demoInsertSp", 0, nameParam, addrParam);

		logger.debug(String.valueOf(row));

		// ResultSet rs = spDAO.fetchBySp(null, "demoSelectSp",
		// 0, inputParam);
		//
		// while(rs.next()){
		// logger.debug(rs.getString(1));
		// }

	}
}
