package com.ctrip.platform.bll;

import com.ctrip.platform.dao.PersonDAO;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class UpdatePersonBLL {
	public static void main(String[] args) throws Exception {
		try {
			PersonDAO person = new PersonDAO();

			person.setUseDBClient(false);

			Parameter addrParam = ParameterFactory.createStringParameter(1,
					"hihihi");
			
			Parameter nameParam = ParameterFactory.createStringParameter(2,
					"1");
			
			int row = person.SetAddrByName(addrParam, nameParam);
			System.out.println("Affect row count: " + row);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
