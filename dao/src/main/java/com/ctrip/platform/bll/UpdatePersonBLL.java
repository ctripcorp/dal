package com.ctrip.platform.bll;

import com.ctrip.platform.dao.msg.AvailableType;
import com.ctrip.platform.dao.tabledao.PersonDAO;

public class UpdatePersonBLL {
	public static void main(String[] args) throws Exception {
		try {
			System.out.println(".......a....");
			PersonDAO person = new PersonDAO();

			person.setUseDBClient(true);

			AvailableType addrParam = new<String> AvailableType(1, "world");
			AvailableType nameParam = new<String> AvailableType(2, "1");
			// AvailableType genderParam = new <Integer> AvailableType(2, 1);
			System.out.println(".......a1....");
			int row = person.SetAddrByName(addrParam, nameParam);
			System.out.println("Affect row count" + row);
			System.out.println(".......a...2.");
			// ResultSet rs = person.SelAddrTelByNameEqGenderEq(nameParam,
			// genderParam);
			// while (rs.next()) {
			// System.out.println(rs.getString(1));
			// System.out.println(rs.getString(2));
			// }
			System.out.println("......bb......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
