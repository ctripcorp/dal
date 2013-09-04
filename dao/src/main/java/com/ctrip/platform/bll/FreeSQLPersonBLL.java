package com.ctrip.platform.bll;

import java.sql.ResultSet;

import com.ctrip.platform.dao.msg.AvailableType;
import com.ctrip.platform.dao.sqldao.FreeSQLPersonDAO;

public class FreeSQLPersonBLL {
	
	public static void main(String[] args) throws Exception {
		
//		Object a = new int[]{1,2};
//		
//		System.out.println(a.getClass().isArray());
		
		try {
			FreeSQLPersonDAO person = new FreeSQLPersonDAO();

			person.setUseDBClient(false);

			AvailableType nameParam = new AvailableType(1, "1");
			AvailableType genderParam = new AvailableType(2, new int[]{1,2});

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
