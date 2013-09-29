package com.ctrip.platform.bll;

import com.ctrip.platform.dao.DAOFactory;
import com.ctrip.platform.dao.PersonDAO;
import com.ctrip.platform.dao.enums.FlagsEnum;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class UpdatePersonBLL {
	public static void main(String[] args) throws Exception {
		try {
			PersonDAO person = DAOFactory.createPersonDAO();

			person.setUseDBClient(false);

			Parameter addrParam = ParameterFactory.createStringParameter(1,
					"hihihi");
			
			Parameter nameParam = ParameterFactory.createStringParameter(2,
					"1");
			
			int row = person.SetAddrByName(FlagsEnum.TEST.getIntVal(), 
					addrParam, nameParam);
			System.out.println("Affect row count: " + row);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
