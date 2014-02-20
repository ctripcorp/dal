package com.ctrip.platform.dal.tester.manyTypes;

import java.io.File;

import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.tester.person.DalPersonParser;
import com.ctrip.platform.dal.tester.person.Person;

public class ManyuTypesTest {
	public static void main(String[] args) {
		Configuration.addResource("conf.properties");
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClientFactory.initDirectClientFactory(reader, "HtlProductdb", "dao_test");
		} catch (Exception e) {
			System.exit(0);
		}

		try {
			DalHints hints = new DalHints();
			
			DalTableDao<Manytypes> dao = new DalTableDao<Manytypes>(new DalManytypesParser());
			Manytypes p = new Manytypes();
			p.setBigIntCol(1L);
			p.setBinaryCol("BinaryCol".getBytes());
			dao.insert(hints, p, p, p);
			dao.query("id > 0", null, null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
