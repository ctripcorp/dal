package com.ctrip.platform.dal.tester.person;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalTableDao;

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
