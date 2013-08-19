package com.ctrip.sysdev.dao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ctrip.sysdev.enums.AvailableTypeEnum;
import com.ctrip.sysdev.msg.AvailableType;

public class PersonDAO extends BaseDAO {

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
	public ResultSet SelAddrTelByNameEqGenderEq(String name, int gender)
			throws Exception {

		String sql = "SELECT Address, Telephone FROM Person WHERE Name = ? AND Gender = ?";

		DAOFunction func = new DAOFunction();

		// func.setFields(new String[]{ "Address", "Telephone" });
		//
		// Map<Integer, Class<?>> requiredParams = new TreeMap<Integer,
		// Class<?>>();
		// requiredParams.put(0, String.class);
		// requiredParams.put(1, int.class);
		//
		Map<Integer, AvailableTypeEnum> resultFields = new TreeMap<Integer, AvailableTypeEnum>();
		resultFields.put(1, AvailableTypeEnum.STRING);
		resultFields.put(2, AvailableTypeEnum.STRING);

		//
		// func.setRequiredParams(requiredParams);
		func.setResultFields(resultFields);
		//
		func.setSql(sql);

		// return func;

		LinkedList<AvailableType> params = new LinkedList<AvailableType>();

		AvailableType nameType = new AvailableType();
		nameType.currentType = AvailableTypeEnum.STRING;
		nameType.string_arg = "1";

		AvailableType genderType = new AvailableType();
		genderType.currentType = AvailableTypeEnum.LONG;
		genderType.long_arg = 1;

		params.add(nameType);
		params.add(genderType);

		return super.fetch(null, func, params, 0);

	}

	public static void main(String[] args) throws Exception {
		PersonDAO person = new PersonDAO();

		person.setDbClient(true);

		ResultSet rs = person.SelAddrTelByNameEqGenderEq("1", 1);
		while (rs.next()) {
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
		}
	}

}
