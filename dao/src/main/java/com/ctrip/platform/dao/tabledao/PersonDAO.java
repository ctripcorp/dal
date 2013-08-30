package com.ctrip.platform.dao.tabledao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.common.AbstractDAO;
import com.ctrip.platform.dao.exception.ParametersInvalidException;
import com.ctrip.platform.dao.msg.AvailableType;

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
	public ResultSet SelAddrTelByNameEqGenderEq(AvailableType... params)
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
	public int SetAddrByName(AvailableType... params) throws Exception {

		final int paramCount = 2;

		final String sql = "UPDATE Person SET Address = ? WHERE Name = ?";

		if (params.length != paramCount) {
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", paramCount,
					params.length));
		}

		return super.execute(null, sql, 0, params);
	}

	public static void main(String[] args) throws Exception {
		try {
			System.out.println(".......a....");
			PersonDAO person = new PersonDAO();

			person.setUseDBClient(false);

			AvailableType addrParam = new<String> AvailableType(1, "world");
			AvailableType nameParam = new<String> AvailableType(2, "1");
			// AvailableType genderParam = new <Integer> AvailableType(2, 1);
			System.out.println(".......a1....");
			int row = person.SetAddrByName(addrParam, nameParam);
			System.out.println(".......a...2.");
			// ResultSet rs = person.SelAddrTelByNameEqGenderEq(nameParam,
			// genderParam);
			// while (rs.next()) {
			// System.out.println(rs.getString(1));
			// System.out.println(rs.getString(2));
			// }
			System.out.println("......bb......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
