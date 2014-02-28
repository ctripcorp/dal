package com.ctrip.sysdev.das.tester.person;

import com.ctrip.platform.dao.DalClientFactory;
import com.ctrip.platform.dao.DalTableDao;

public class PersonTest {
	public static void main(String[] args) {
		try {
			DalClientFactory.initDirectClientFactory(null, "htlpr", "sss", "abc");
			
			DalTableDao<Person> dao = new DalTableDao<Person>(new DalPersonParser());
			
			dao.insert(null);
			dao.query(null, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
