package com.ctrip.sysdev.apptools.dao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.apptools.dao.common.AbstractDAO;
import com.ctrip.sysdev.apptools.dao.exception.ParametersInvalidException;
import com.ctrip.sysdev.apptools.dao.msg.AvailableType;

public class PersonDAO extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonDAO.class);

	private Map<String, String> dbField2POJOField;

	public PersonDAO() {
		dbField2POJOField = new HashMap<String, String>();
		// dbField2POJOField.put("Name", "name");
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
		
		if(params.length != paramCount){
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", 
					paramCount, params.length));
		}

		return super.fetch(null, sql, 0, params);
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int SetAddrByName(AvailableType... params)
			throws Exception{
		
		final int paramCount = 2;
		
		final String sql = "UPDATE Person SET Address = ? WHERE Name = ?";
		
		if(params.length != paramCount){
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", 
					paramCount, params.length));
		}
		
		return super.execute(null, sql, 0, params);
	}

	public static void main(String[] args) throws Exception {
		PersonDAO person = new PersonDAO();

		person.setUseDBClient(true);
		
		AvailableType addrParam = new <String> AvailableType(1, "world");
		AvailableType nameParam = new <String> AvailableType(2, "1");
//		AvailableType genderParam = new <Integer> AvailableType(2, 1);
		
		int row = person.SetAddrByName(addrParam, nameParam);
		
		logger.info(logger.getClass().getName());
		
		logger.info(String.valueOf(row));

//		ResultSet rs = person.SelAddrTelByNameEqGenderEq(nameParam, genderParam);
//		while (rs.next()) {
//			System.out.println(rs.getString(1));
//			System.out.println(rs.getString(2));
//		}
	}

}
