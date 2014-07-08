package com.ctrip.platform.dal.tester.person;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;

public class PersonTest {
	public static void main(String[] args) {
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			System.exit(0);
		}

		try {
			DalHints hints = new DalHints();
			
			DalTableDao<Person> dao = new DalTableDao<Person>(new DalPersonParser());
			Person p = new Person();
			p.setAddress("Address");
			p.setName("Name");
			dao.insert(hints, p, p, p);
//			dao.query(null, null, null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}