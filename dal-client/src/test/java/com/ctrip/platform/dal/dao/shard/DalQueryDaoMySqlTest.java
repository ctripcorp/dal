package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.MultipleSqlBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DalQueryDaoMySqlTest extends DalQueryDaoTest {
	
	public DalQueryDaoMySqlTest() {
		super(DATABASE_NAME_MYSQL, DatabaseCategory.MySql);
	}

	private final static String DATABASE_NAME_MYSQL = "dao_test_mysql_dbShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private final static String GENERATED_KEY = "GENERATED_KEY";
	
	private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"("
			+ "id int NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private static DalClient clientMySql;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { DROP_TABLE_SQL_MYSQL_TPL, CREATE_TABLE_SQL_MYSQL_TPL};
			clientMySql.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { DROP_TABLE_SQL_MYSQL_TPL};
			clientMySql.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[3];
			for(int j = 0; j < 3; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j] = "INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
							+ " VALUES(" + id + ", " + quantity + "," + i + ", 'SH INFO')";
			}
			int[] counts = clientMySql.batchUpdate(insertSqls, hints.inShard(i));
			assertArrayEquals(new int[] { 1, 1, 1 }, counts);
		}
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			for(int i = 0; i < mod; i++) {
				clientMySql.update(sql, parameters, hints.inShard(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
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
        String[] sqls = new String[4];
        sqls[0] = "SET @id = ?,@type = ?";
        sqls[1] = "select id from " + TABLE_NAME + " where id = @id and type = @type";
        sqls[2] = "select type from " + TABLE_NAME + " where id = @id and type = @type";
        sqls[3] = "select quantity from " + TABLE_NAME + " where id = @id and type = @type";
        int i = 0;
        try {
            MultipleSqlBuilder multipleSqlBuilder = new MultipleSqlBuilder();
            for (String sql : sqls) {
                ++i;
                if (i == 1) {
                    StatementParameters parameters = new StatementParameters();
                    parameters.set(1, Types.INTEGER,1);
                    parameters.set(2, Types.INTEGER,0);
                    multipleSqlBuilder.addQuery(sql, parameters, new MyDalResultSetExtractor(), new DalListMerger<>());
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
        assertEquals(4, list.size());
        assertEquals(0, ((List)list.get(0)).size());
        assertEquals(1, ((List)list.get(1)).size());
        assertEquals(1, ((List)list.get(2)).size());
        assertEquals(1, ((List)list.get(3)).size());
    }
}
