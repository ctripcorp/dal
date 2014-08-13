package com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class SqlServerUnitBase {
	
	protected static DalClient client = null;
	protected static ClientTestDalRowMapper mapper = null;
	protected final static String DATABASE_NAME = "HotelPubDB";
	protected final static String TABLE_NAME = "dal_client_test";
	protected final static String SP_I_NAME = "dal_client_test_i";
	protected final static String SP_D_NAME="dal_client_test_d";
	protected final static String SP_U_NAME = "dal_client_test_u";
	
	private final static String DROP_TABLE_SQL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	//Only has normal parameters
	private static final String CREATE_I_SP_SQL = "CREATE PROCEDURE " + SP_I_NAME + "("
			+ "@quantity int,"
			+ "@type smallint,"
			+ "@address VARCHAR(64)) "
			+ "AS BEGIN INSERT INTO " + TABLE_NAME
			+ "([quantity], [type], [address]) "
			+ "VALUES(@quantity, @type, @address);"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	//Has out parameters store procedure
	private static final String CREATE_D_SP_SQL = "CREATE PROCEDURE " + SP_D_NAME + "("
			+ "@dal_id int,"
			+ "@count int OUTPUT)"
			+ "AS BEGIN DELETE FROM " + TABLE_NAME
			+ " WHERE [id]=@dal_id;"
			+ "SELECT @count = COUNT(*) from " + TABLE_NAME + ";"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	//Has in-out parameters store procedure
	private static final String CREATE_U_SP_SQL = "CREATE PROCEDURE " + SP_U_NAME + "("
			+ "@dal_id int,"
			+ "@quantity int,"
			+ "@type smallint,"
			+ "@last_changed datetime,"
			+ "@address VARCHAR(64) OUTPUT)"
			+ "AS BEGIN UPDATE " + TABLE_NAME +" "
			+ "SET [quantity] = @quantity, [type]=@type, [address]=@address, [last_changed]=@last_changed "
			+ "WHERE [id]=@dal_id;"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	
	private static final String DROP_I_SP_SQL = "IF OBJECT_ID('dbo." + SP_I_NAME + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + SP_I_NAME;
	private static final String DROP_D_SP_SQL = "IF OBJECT_ID('dbo." + SP_D_NAME + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + SP_D_NAME;
	private static final String DROP_U_SP_SQL = "IF OBJECT_ID('dbo." + SP_U_NAME + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + SP_U_NAME;

	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
			mapper = new ClientTestDalRowMapper();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL, 
				DROP_I_SP_SQL, CREATE_I_SP_SQL,
				DROP_D_SP_SQL, CREATE_D_SP_SQL,
				DROP_U_SP_SQL, CREATE_U_SP_SQL };
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, DROP_I_SP_SQL, DROP_D_SP_SQL, DROP_U_SP_SQL};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}
	
	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"SET IDENTITY_INSERT "+ TABLE_NAME +" ON",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(1, 10, 1, 'SH INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(2, 11, 1, 'BJ INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(3, 12, 2, 'SZ INFO')",
				"SET IDENTITY_INSERT "+ TABLE_NAME +" OFF"};
		client.batchUpdate(insertSqls, hints);
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.update(sql, parameters, hints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Get the models all in dal_client_test by specified IDs
	 * 
	 * @param ids
	 * @return The list of ClientTestModel
	 */
	protected List<ClientTestModel> queryModelsByIds(int... ids) {
		List<ClientTestModel> models = new ArrayList<ClientTestModel>();
		String querySql = "";
		if (null != ids && ids.length > 0) {
			Integer[] idds = new Integer[ids.length];
			for (int i = 0; i < idds.length; i++) {
				idds[i] = ids[i];
			}
			querySql = "SELECT * FROM %s WHERE id in(%s)";
			String inClause = StringUtils.join(idds, ",");
			querySql = String.format(querySql, TABLE_NAME, inClause);
		} else {
			querySql = "SELECT * FROM " + TABLE_NAME;
		}
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			models = client.query(querySql, parameters, hints,
					new DalRowMapperExtractor<ClientTestModel>(mapper));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return models;
	}
}
