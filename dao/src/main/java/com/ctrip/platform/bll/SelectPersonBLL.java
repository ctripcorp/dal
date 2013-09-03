package com.ctrip.platform.bll;

import java.sql.ResultSet;

import com.ctrip.platform.dao.msg.AvailableType;
import com.ctrip.platform.dao.tabledao.PersonDAO;

public class SelectPersonBLL {
	public static void main(String[] args) throws Exception {
		try {
			PersonDAO person = new PersonDAO();

			person.setUseDBClient(false);

			AvailableType addrParam = new<String> AvailableType(1, "1");
			AvailableType nameParam = new<Integer> AvailableType(2, 1);

			ResultSet rs = person.SelAddrTelByNameEqGenderEq(addrParam,
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
