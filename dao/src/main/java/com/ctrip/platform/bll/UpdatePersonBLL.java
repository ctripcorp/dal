package com.ctrip.platform.bll;

import com.ctrip.platform.dao.PersonDAO;
import com.ctrip.platform.dao.msg.AvailableType;

public class UpdatePersonBLL {
	public static void main(String[] args) throws Exception {
		try {
			PersonDAO person = new PersonDAO();

			person.setUseDBClient(false);

			AvailableType addrParam = new AvailableType(1, "hihihi");
			AvailableType nameParam = new AvailableType(2, "1");

			int row = person.SetAddrByName(addrParam, nameParam);
			System.out.println("Affect row count: " + row);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
