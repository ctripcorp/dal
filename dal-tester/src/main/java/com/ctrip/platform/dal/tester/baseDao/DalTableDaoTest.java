package com.ctrip.platform.dal.tester.baseDao;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.tester.person.DalPersonParser;
import com.ctrip.platform.dal.tester.person.Person;

public class DalTableDaoTest {
	private StatementParameters parameters = new StatementParameters();
	private DalHints hints = new DalHints();
	DalParser<Person> personParser = new DalPersonParser();
	
	public void testQueryByPkNumber() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			Person p = dao.queryByPk(2922, hints);
			System.out.println(p.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testQueryByPk() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			Person p = new Person();
			p.setID(2922);
			p = dao.queryByPk(p, hints);
			System.out.println(p.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testQueryLike() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			Person p = new Person();
			p.setName("aaaaa");
			p.setAddress("aaa");

			List<Person> pList = dao.queryLike(p, hints);
			System.out.println(pList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void testQuery() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 2923);

			List<Person> pList = dao.query("ID = ?", parameters, hints);
			System.out.println(pList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void testRange() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 13);
			
			Person p = dao.queryFirst("ID > ?", parameters, hints);
			System.out.println(p.getID());
			
			List<Person> result = dao.queryTop("ID > ?", parameters, hints, 5);
			System.out.println(result.size());
			
			result = dao.queryFrom("ID > ?", parameters, hints, 3, 5);
			System.out.println(result.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
        LogConfig.setAppID("9302011");
        LogConfig.setLoggingServerIP("192.168.82.58");
        LogConfig.setLoggingServerPort("63100");

		Configuration.addResource("conf.properties");
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClientFactory.initDirectClientFactory(reader, "HtlProductdb", "dao_test");
		} catch (Exception e) {
			System.exit(0);
		}
		
		DalTableDaoTest test = new DalTableDaoTest();
		
		
		test.testQueryByPkNumber();
		test.testQueryByPk();
		test.testQueryLike();
		test.testQuery();
		test.testRange();
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
