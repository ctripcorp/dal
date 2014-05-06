package com.ctrip.platform.dal.tester.person;

import java.io.File;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;

public class PersonTest {
	public static void main(String[] args) {
        LogConfig.setAppID("929143");
//      LogConfig.setLoggingServerIP("localhost");
        LogConfig.setLoggingServerIP("192.168.82.58");
        LogConfig.setLoggingServerPort("63100");

		Configuration.addResource("conf.properties");
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClientFactory.initDirectClientFactory(reader, "HtlProductdb", "dao_test");
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
