package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.MultipleSqlBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DalQueryDaoSqlSvrTest extends DalQueryDaoTest {

	public DalQueryDaoSqlSvrTest() {
		super(DATABASE_NAME_SQLSVR, DatabaseCategory.SqlServer);
	}
	
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr_dbShard";
	private final static String DATABASE_NAME_MOD = DATABASE_NAME_SQLSVR;
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	
	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int, "
			+ "type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	private static DalClient clientSqlSvr;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			clientSqlSvr = DalClientFactory.getClient(DATABASE_NAME_SQLSVR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		// For SQL server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			sqls = new String[] {DROP_TABLE_SQL_SQLSVR_TPL, CREATE_TABLE_SQL_SQLSVR_TPL};
			for (int j = 0; j < sqls.length; j++) {
				clientSqlSvr.update(sqls[j], parameters, hints.inShard(i));
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		//For Sql Server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			clientSqlSvr.update(DROP_TABLE_SQL_SQLSVR_TPL, parameters, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[2 + 3];
			insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + " ON";
			for(int j = 0; j < 3; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j + 1] = "INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
							+ " VALUES(" + id + ", " + quantity + "," + i + ", 'SH INFO')";
			}
					
			insertSqls[4] = "SET IDENTITY_INSERT "+ TABLE_NAME +" OFF";
			clientSqlSvr.batchUpdate(insertSqls, hints.inShard(i));
		}
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		sql = "DELETE FROM " + TABLE_NAME;
		parameters = new StatementParameters();
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			clientSqlSvr.update(sql, parameters, hints.inShard(i));
		}
	}

	@Override
	public void insertBack() {
		try {
			setUp();
		} catch (Exception e) {
			fail();
		}
	}

	@Override
	public List queryMultipleAllShardsUseLocalVariable(DalHints hints) throws SQLException {
		String declareSql = "DECLARE @id int,@type int";
		String[] sqls = new String[4];
		//这其实是两个sql，用;分隔开，方便参数化设置
		sqls[0] = "SET @id = ?;SET @type = ?";
		sqls[1] = "select id from " + TABLE_NAME + " where id = @id and type = @type";
		sqls[2] = "select type from " + TABLE_NAME + " where id = @id and type = @type";
		sqls[3] = "select quantity from " + TABLE_NAME + " where id = @id and type = @type";

		int i = 0;
		try {
			MultipleSqlBuilder multipleSqlBuilder = new MultipleSqlBuilder();
			//针对sqlserver 需要先声明变量，才能set
			multipleSqlBuilder.addQuery(declareSql, new StatementParameters());
			for (String sql : sqls) {
				++i;
				if (i == 1) {
					StatementParameters parameters = new StatementParameters();
					parameters.set(1, Types.INTEGER,1);
					parameters.set(2, Types.INTEGER,0);
					multipleSqlBuilder.addQuery(sql, parameters);
				}
				else {
					multipleSqlBuilder.addQuery(sql, new StatementParameters(), new DalObjectRowMapper<Object>());
				}
			}
			return dao.query(multipleSqlBuilder, hints.inAllShards());
		} catch (Exception e) {
			throw new SQLException();
		}
	}

    @Override
    public void assertMultipleResult1(List list) {
        assertEquals(3, list.size());
        assertEquals(1, ((List)list.get(0)).size());
        assertEquals(1, ((List)list.get(1)).size());
        assertEquals(1, ((List)list.get(2)).size());
    }
}
