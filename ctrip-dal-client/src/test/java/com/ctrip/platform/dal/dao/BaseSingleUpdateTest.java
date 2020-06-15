package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.DefaultTaskContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.task.SingleTask;

public abstract class BaseSingleUpdateTest {
    public abstract void setOptionTest();
	private static final String CALL_SP_BY_NAME = "callSpbyName";
	private static final String CALL_SP_BY_SQLSEVER = "callSpbySqlServerSyntax";
	private static final String CALL_SPT = "callSpt";
    private <T> SingleTask<T> getTest(DalParser<T> parser) {
		CtripTaskFactory ctripTaskFactory=new CtripTaskFactory();
		ctripTaskFactory.setCallSpt(false);
		ctripTaskFactory.setCallSpbySqlServerSyntax(true);
		ctripTaskFactory.setCallSpByName(false);
		Map<String,String> settings=new HashMap<>();
		settings.put(CALL_SP_BY_NAME,"false");
		settings.put(CALL_SP_BY_SQLSEVER,"true");
		settings.put(CALL_SPT,"false");
		ctripTaskFactory.setCtripTaskSettings(settings);
        return ctripTaskFactory.createSingleUpdateTask(parser);
    }


	private final static String DATABASE_NAME = "SimpleShard";
	
	private final static String TABLE_NAME = "People";
	
	private static DalClient client;
	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
        setOptionTest();
		String[] insertSqls = new String[4];
		insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + " ON";
		insertSqls[1] = "DELETE FROM " + TABLE_NAME;
		insertSqls[2] = "INSERT INTO " + TABLE_NAME +" ([PeopleID], [Name], [CityID], [ProvinceID], [CountryID])"
					+ " VALUES(" + 1 + ", " + "'test name' , 1, 1, 1)";
		insertSqls[3] = "SET IDENTITY_INSERT "+ TABLE_NAME + " OFF";
		client.batchUpdate(insertSqls, new DalHints().inShard(0));
		setUpShard();
	}

	public void setUpShard(){
		try {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					String tableName = TABLE_NAME + "_" + j;
					DalHints hints = new DalHints().inShard(i);
					String[] insertSqls = null;
					insertSqls = new String[6];
					insertSqls[0] = "SET IDENTITY_INSERT "+ tableName + " ON";
					insertSqls[1] = "DELETE FROM " + tableName;
					for(int k = 0; k < 3; k ++) {
						int id = k;
						insertSqls[k+2] = "INSERT INTO " + tableName +" ([PeopleID], [Name], [CityID], [ProvinceID], [CountryID])"
								+ " VALUES(" + id + ", " + "'test name' , " + j + ", 1, " + i + ")";
					}
					insertSqls[5] = "SET IDENTITY_INSERT "+ tableName + " OFF";
					client.batchUpdate(insertSqls, hints);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		client.update("DELETE FROM " + TABLE_NAME, new StatementParameters(), new DalHints().inShard(0));
		tearDownShard();
	}
	
	public void tearDownShard() throws Exception {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				String tableName = TABLE_NAME + "_" + j;
				client.update("DELETE FROM " + tableName, new StatementParameters(), new DalHints().inShard(i));
			}
		}
	}

	@Test
    public void testPrepareSpCallUpdateIgnoreNullColumns() {
	    String spName = "spA_test_u";
        StatementParameters parameters = new StatementParameters();
        PeopleParser parser = new PeopleParser();
        SingleUpdateSpaTask<People> singleTask = new SingleUpdateSpaTask<>();
        Map<String,String> settings=new HashMap<>();
        settings.put(CALL_SP_BY_NAME,"false");
        settings.put(CALL_SP_BY_SQLSEVER,"true");
        settings.put(CALL_SPT,"false");
        singleTask.initTaskSettings(settings);
        singleTask.initialize(parser);

        String noCityIDColumn = "exec spA_test_u @PeopleID=?, @Name=?, @ProvinceID=?, @CountryID=?";
        People p1 = new People();
        p1.setPeopleID((long)1);
        p1.setName("test");
        p1.setCityID(null);
        p1.setProvinceID(-1);
        p1.setCountryID(-1);
        String callSql1 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p1));
        Assert.assertEquals(callSql1, noCityIDColumn);

        String noProvinceIDColumn = "exec spA_test_u @PeopleID=?, @Name=?, @CityID=?, @CountryID=?";
        People p2 = new People();
        p2.setPeopleID((long)1);
        p2.setName("test");
        p2.setCityID(-1);
        p2.setProvinceID(null);
        p2.setCountryID(-1);
        String callSql2 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p2));
        Assert.assertEquals(callSql2, noProvinceIDColumn);

        String noNameCountryIDColumn = "exec spA_test_u @PeopleID=?, @CityID=?, @ProvinceID=?";
        People p3 = new People();
        p3.setPeopleID((long)1);
        p3.setName(null);
        p3.setCityID(-1);
        p3.setProvinceID(-1);
        p3.setCountryID(null);
        String callSql3 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p3));
        Assert.assertEquals(callSql3, noNameCountryIDColumn);

		String noPeopleIDColumn = "exec spA_test_u @PeopleID=?, @CityID=?, @ProvinceID=?";
		People p4 = new People();
		p4.setName(null);
		p4.setCityID(-1);
		p4.setProvinceID(-1);
		p4.setCountryID(null);
		String callSql4 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p4));
		Assert.assertEquals(callSql4, noPeopleIDColumn);

		String noColumns = "exec spA_test_u @PeopleID=?";
		People p5 = new People();
		String callSql5 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p5));
		Assert.assertEquals(callSql5, noColumns);
    }

    @Test
    public void testPrepareSpCallInsertNotIgnoreNullColumns() {
        String spName = "spA_test_i";
        StatementParameters parameters = new StatementParameters();
        PeopleParser parser = new PeopleParser();
        SingleInsertSpaTask<People> singleTask = new SingleInsertSpaTask<>();
        Map<String,String> settings=new HashMap<>();
        settings.put(CALL_SP_BY_NAME,"false");
        settings.put(CALL_SP_BY_SQLSEVER,"true");
        settings.put(CALL_SPT,"false");
        singleTask.initTaskSettings(settings);
        singleTask.initialize(parser);

        String noCityIDColumn = "exec spA_test_i @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?";
        People p1 = new People();
        p1.setPeopleID((long)1);
        p1.setName("test");
        p1.setCityID(null);
        p1.setProvinceID(-1);
        p1.setCountryID(-1);
        String callSql1 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p1));
        Assert.assertEquals(callSql1, noCityIDColumn);

        String noProvinceIDColumn = "exec spA_test_i @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?";
        People p2 = new People();
        p2.setPeopleID((long)1);
        p2.setName("test");
        p2.setCityID(-1);
        p2.setProvinceID(null);
        p2.setCountryID(-1);
        String callSql2 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p2));
        Assert.assertEquals(callSql2, noProvinceIDColumn);

        String noNameCountryIDColumn = "exec spA_test_i @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?";
        People p3 = new People();
        p3.setPeopleID((long)1);
        p3.setName(null);
        p3.setCityID(-1);
        p3.setProvinceID(-1);
        p3.setCountryID(null);
        String callSql3 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p3));
        Assert.assertEquals(callSql3, noNameCountryIDColumn);
    }

    @Test
    public void testPrepareSpCallDeleteNotIgnoreNullColumns() {
        String spName = "spA_test_d";
        StatementParameters parameters = new StatementParameters();
        PeopleParser parser = new PeopleParser();
        SingleDeleteSpaTask<People> singleTask = new SingleDeleteSpaTask<>();
        Map<String,String> settings=new HashMap<>();
        settings.put(CALL_SP_BY_NAME,"false");
        settings.put(CALL_SP_BY_SQLSEVER,"true");
        settings.put(CALL_SPT,"false");
        singleTask.initTaskSettings(settings);
        singleTask.initialize(parser);

        String noCityIDColumn = "exec spA_test_d @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?";
        People p1 = new People();
        p1.setPeopleID((long)1);
        p1.setName("test");
        p1.setCityID(null);
        p1.setProvinceID(-1);
        p1.setCountryID(-1);
        String callSql1 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p1));
        Assert.assertEquals(callSql1, noCityIDColumn);

        String noProvinceIDColumn = "exec spA_test_d @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?";
        People p2 = new People();
        p2.setPeopleID((long)1);
        p2.setName("test");
        p2.setCityID(-1);
        p2.setProvinceID(null);
        p2.setCountryID(-1);
        String callSql2 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p2));
        Assert.assertEquals(callSql2, noProvinceIDColumn);

        String noNameCountryIDColumn = "exec spA_test_d @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?";
        People p3 = new People();
        p3.setPeopleID((long)1);
        p3.setName(null);
        p3.setCityID(-1);
        p3.setProvinceID(-1);
        p3.setCountryID(null);
        String callSql3 = singleTask.prepareSpCall(spName, parameters, parser.getFields(p3));
        Assert.assertEquals(callSql3, noNameCountryIDColumn);
    }

	@Test
	public void testExecute() {
		PeopleParser parser = new PeopleParser();
		SingleTask<People> test = getTest(parser);
		
		People p1 = new People();
	 	p1.setPeopleID((long)1);
	 	p1.setName("test");
	 	p1.setCityID(-1);
	 	p1.setProvinceID(-1);
	 	p1.setCountryID(-1);

		try {
			test.execute(new DalHints().inShard(0), parser.getFields(p1), p1, new DefaultTaskContext());
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testExecuteNullPk() {
		PeopleParser parser = new PeopleParser();
		SingleTask<People> test = getTest(parser);

		People p1 = new People();
		p1.setName("test");
		p1.setCityID(-1);
		p1.setProvinceID(-1);
		p1.setCountryID(-1);

		try {
			Assert.assertEquals(100,
					test.execute(new DalHints().inShard(0), parser.getFields(p1), p1, new DefaultTaskContext()));
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testExecuteShard() {
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);
		SingleTask<People> test = getTest(parser);
		
		try {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					List<People> p = dao.query("1=1", new StatementParameters(), hints);
					for(People p1: p) {
					 	p1.setName("test123");
					 	p1.setProvinceID(-100);
						test.execute(new DalHints(), parser.getFields(p1), p1, new DefaultTaskContext());
					}
					
					p = dao.query("1=1", new StatementParameters(), hints);
					for(People p1: p) {
						Assert.assertEquals(p1.getName(), "test123");
						Assert.assertEquals(p1.getProvinceID().intValue(), -100);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testExecuteShardByDao() {
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);
		
		try {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					List<People> p = dao.query("1=1", new StatementParameters(), hints);
					for(People p1: p) {
					 	p1.setName("test123");
					 	p1.setProvinceID(-100);
					}
					
					hints = new DalHints().inShard(i).inTableShard(j);
					dao.update(new DalHints(), p);
					
					hints = new DalHints().inShard(i).inTableShard(j);
					p = dao.query("1=1", new StatementParameters(), hints);
					for(People p1: p) {
						Assert.assertEquals(p1.getName(), "test123");
						Assert.assertEquals(p1.getProvinceID().intValue(), -100);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testExecuteShardByDao2() {
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);
		
		try {
			List<People> pAll = new ArrayList<>();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					List<People> p = dao.query("1=1", new StatementParameters(), hints);
					for(People p1: p) {
					 	p1.setName("test123");
					 	p1.setProvinceID(-100);
					}
					
					pAll.addAll(p);
				}
			}
			
			dao.update(new DalHints(), pAll);
					
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					List<People> p = dao.query("1=1", new StatementParameters(), hints);
					for(People p1: p) {
						Assert.assertEquals(p1.getName(), "test123");
						Assert.assertEquals(p1.getProvinceID().intValue(), -100);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	private <T> Map<Integer, Map<String, ?>> getPojosFields(List<T> daoPojos, DalParser<T> parser) {
		Map<Integer, Map<String, ?>> pojoFields = new HashMap<>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		int i = 0;
		for (T pojo: daoPojos){
			pojoFields.put(i++, parser.getFields(pojo));
		}
		
		return pojoFields;
	}

}
