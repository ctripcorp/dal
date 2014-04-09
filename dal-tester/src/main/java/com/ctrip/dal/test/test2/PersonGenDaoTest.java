package com.ctrip.dal.test.test2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.*;

public class PersonGenDaoTest {
	public static void main(String[] args) {
		/*
		try {
			*//**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**//*
			DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			//DalClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			PersonGenDao dao = new PersonGenDao();
		
			PersonGen pk = dao.queryByPk(123456);// you value here
			
			pk = dao.queryByPk(pk);
			List<PersonGen> pojos = dao.queryByPage(pk, 100, 0);



			// Test additional customized method
			int affectedRows = 0;
			List<PersonGen> results = null;
			// Test updatePerson
			Integer ID = ${p.getValidationValue()};
			String Telephone = "null";
			String Name = "null";

    		affectedRows = dao.updatePerson(ID, Telephone, Name});

			// Test deletePersonById
			Integer ID = ${p.getValidationValue()};

    		affectedRows = dao.deletePersonById(ID});

			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} */
	}

}