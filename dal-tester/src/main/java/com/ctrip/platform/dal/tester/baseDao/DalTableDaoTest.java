package com.ctrip.platform.dal.tester.baseDao;

import java.io.File;
import java.sql.Types;
import java.util.List;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
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
			Person p = dao.queryByPk(1000, hints);
			System.out.println(p.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testQueryByPk() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			Person p = new Person();
			p.setID(1000);
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

	public void testInsert() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			
			Person p = new Person();
			p.setName("insert test 1");
			dao.insert(hints, p);
			p.setName("insert test 2");
			dao.insert(hints, p);
			p.setName("insert test 3");
			dao.insert(hints, p);
			
			Person[] pList = new Person[3];
			p = new Person();
			p.setName("insert test 4");
			pList[0] = p;
			p = new Person();
			p.setName("insert test 5");
			pList[1] = p;
			p = new Person();
			p.setName("insert test 6");
			pList[2] = p;
			
			dao.insert(hints, pList);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testUpdate() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "insert%");
			
			Person p = dao.queryFirst("Name Like ?", parameters, hints);
			System.out.println(p.getID());
			p.setAddress("Never mind it");
			dao.update(hints, p);
			p = dao.queryByPk(p.getID(), hints);
			System.out.println(p.getAddress());
			
			parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "update test");
			parameters.set(2, Types.INTEGER, p.getID());
			dao.update("update Person set Address = ? where ID >= ?", parameters, hints);
			p = dao.queryByPk(p.getID(), hints);
			System.out.println(p.getAddress());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDelete() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "update test");
			
			Person p = dao.queryFirst("Address Like ?", parameters, hints);
			System.out.println(p.getID());
			dao.delete(hints, p);
			try{
				p = dao.queryByPk(p.getID(), hints);
				System.out.println(p.getAddress());
			} catch (Exception e) {
				System.out.println("No longer exists");
			}
			
			parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "insert");
			dao.delete("Name LIKE ?", parameters, hints);
			List<Person> result = dao.query("Name LIKE ?", parameters, hints);
			System.out.println(result.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testInsertWithKeyHolder() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			
			dao.delete("Name = 'testInsertKH'", parameters, hints);
			
			Person p = new Person();
			
			Person[] pList = new Person[3];
			p = new Person();
			p.setName("testInsertKH");
			pList[0] = p;
			p = new Person();
			p.setName("testInsertKH");
			pList[1] = p;
			p = new Person();
			p.setName("testInsertKH");
			pList[2] = p;
			
			KeyHolder keyHolder = new KeyHolder();
			dao.insert(hints, keyHolder, pList);
			System.out.println(keyHolder.getKeyList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testCombinedInsert() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			
			dao.delete("Name LIKE 'testInsertCombined%'", parameters, hints);
			
			Person p = new Person();
			
			Person[] pList = new Person[3];
			p = new Person();
			p.setName("testInsertCombined1");
			pList[0] = p;
			p = new Person();
			p.setName("testInsertCombined2");
			pList[1] = p;
			p = new Person();
			p.setName("testInsertCombined3");
			pList[2] = p;
			
			KeyHolder keyHolder = new KeyHolder();
			dao.combinedInsert(hints, keyHolder, pList);
			System.out.println(keyHolder.getKeyList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testContinueOnError() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			
			Person p = new Person();
			Person[] pList = new Person[3];
			p = new Person();
			p.setName("ContinueOnError");
			pList[0] = p;
			p = new Person();
			p.setName("ContinueOnErrorContinueOnErrorContinueOnErrorContinueOnErrorContinueOnError");
			pList[1] = p;
			p = new Person();
			p.setName("ContinueOnError");
			pList[2] = p;
			
			hints = new DalHints(DalHintEnum.continueOnError);
			int count = dao.insert(hints, pList);
			System.out.println(count);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void testBatchInsert() {
		try {
			DalTableDao<Person> dao = new DalTableDao<Person>(personParser);
			
			Person p = new Person();
			try {
				dao.update(hints, p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p.setName("insert test 1");
			dao.insert(hints, p);
			p.setName("insert test 2");
			dao.insert(hints, p);
			p.setName("insert test 3");
			dao.insert(hints, p);
			
			Person[] pList = new Person[3];
			p = new Person();
			p.setName("insert test 4");
			pList[0] = p;
			p = new Person();
			p.setName("insert test 5");
			pList[1] = p;
			p = new Person();
			p.setName("insert test 6");
			pList[2] = p;
			
			dao.batchInsert(hints, pList);			
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
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			System.exit(0);
		}
		
		DalTableDaoTest test = new DalTableDaoTest();
		
//		test.testQueryByPkNumber();
//		test.testQueryByPk();
//		test.testQueryLike();
//		test.testQuery();
//		test.testRange();
//		test.testInsert();
//		test.testUpdate();
//		test.testDelete();
//		test.testInsertWithKeyHolder();
//		test.testCombinedInsert();
//		test.testBatchInsert();
		test.testContinueOnError();
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}