package com.ctrip.platform.bll;

import java.sql.ResultSet;

import com.ctrip.platform.dao.DAOFactory;
import com.ctrip.platform.dao.FreeSQLPersonDAO;
import com.ctrip.platform.dao.enums.FlagsEnum;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class FreeSQLPersonBLL {

	public static void main(String[] args) throws Exception {

		try {
			FreeSQLPersonDAO person = DAOFactory.createFreeSQLPersonDAO();

			person.setUseDBClient(true);

			Parameter nameParam = ParameterFactory
					.createStringParameter(1, "1");
			Parameter genderParam = ParameterFactory.createIntArrayParameter(2,
					new int[] { 1, 2 });

			ResultSet rs = person.getAddrAndTel(FlagsEnum.TEST.getIntVal(),
					genderParam, nameParam);

			while (rs.next()) {
				System.out.println(rs.getString(1));
				System.out.println(rs.getString(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
