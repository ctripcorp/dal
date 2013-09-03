package com.ctrip.platform.bll;

import java.sql.Array;
import java.sql.ResultSet;

import com.ctrip.platform.dao.msg.AvailableType;
import com.ctrip.platform.dao.sqldao.FreeSQLPersonDAO;

public class FreeSQLPersonBLL {
	
	public static void main(String[] args) throws Exception {
		try {
			FreeSQLPersonDAO person = new FreeSQLPersonDAO();

			person.setUseDBClient(false);

			AvailableType nameParam = new<String> AvailableType(1, "1");
			AvailableType genderParam = new<Object> AvailableType(2, );

			ResultSet rs = person.getAddrAndTel(genderParam,
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
