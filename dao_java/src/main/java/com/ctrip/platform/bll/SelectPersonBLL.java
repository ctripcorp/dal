package com.ctrip.platform.bll;

import java.sql.ResultSet;

import com.ctrip.platform.dao.DAOFactory;
import com.ctrip.platform.dao.PersonDAO;
import com.ctrip.platform.dao.enums.FlagsEnum;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class SelectPersonBLL {
	public static void main(String[] args) throws Exception {
		try {
			PersonDAO person = DAOFactory.createPersonDAO();

			person.setUseDBClient(false);
			
			Parameter addrParam = ParameterFactory.createStringParameter(1,
					"1");
			
			Parameter nameParam = ParameterFactory.createIntParameter(2,
					1);

			ResultSet rs = person.getAddrAndTel(FlagsEnum.TEST.getIntVal(),addrParam,
					nameParam);
			
			while (rs.next()) {
				System.out.println(rs.getString(1));
				System.out.println(rs.getString(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
