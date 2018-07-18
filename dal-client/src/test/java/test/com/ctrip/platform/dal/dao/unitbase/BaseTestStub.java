package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import org.junit.Assert;

import test.com.ctrip.platform.dal.dao.unittests.DalTestHelper;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalTableDao;

public class BaseTestStub {
	public static class DatabaseDifference {
		/**
		 * This for very strange issue about diffrence of sql server and oracle
		 * 		String callSql = diff.category == DatabaseCategory.SqlServer ?
				"{call " + SP_D_NAME + "(?,?)}":
					"call " + SP_D_NAME + "(?,?)";
		 */
		public DatabaseCategory category;
		public boolean validateBatchUpdateCount;
		public boolean validateBatchInsertCount;
		public boolean validateReturnCount;
		public boolean supportGetGeneratedKeys;
		public boolean supportMIxOfHardCodeValueAndPlaceholder;
		public boolean supportInsertValues;
		public boolean supportSpIntermediateResult;
		public boolean supportBatchSpWithOutParameter;
	}
	
	public final static String TABLE_NAME = "dal_client_test";
	public final static String SP_WITHOUT_OUT_PARAM = "SP_WITHOUT_OUT_PARAM";
	public final static String SP_WITH_OUT_PARAM = "SP_WITH_OUT_PARAM";
	public final static String SP_WITH_IN_OUT_PARAM = "SP_WITH_IN_OUT_PARAM";
	public final static String SP_WITH_INTERMEDIATE_RESULT = "SP_WITH_INTERMEDIATE_RESULT";

	public String dbName;
	public DatabaseDifference diff;

	public DalClient client = null;
	public ClientTestDalRowMapper mapper = null;
	
	public DalTableDao<ClientTestModel> dao = null;
	
	public BaseTestStub(
			String dbName, 
			DatabaseDifference diff) {
	    this.dbName = dbName;
		this.diff = diff;
		try {
			client = DalClientFactory.getClient(dbName);
			mapper = new ClientTestDalRowMapper();
			dao = new DalTableDao<ClientTestModel>(new ClientTestDalParser(dbName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void assertEquals(int expected, int res, int expAll) throws SQLException {
		if(diff.validateReturnCount) {
			Assert.assertEquals(expected, res);
		}else{
			Assert.assertEquals(expAll, DalTestHelper.getCount(dao));
		}
	}
	
	public void assertEquals(int expected, int res, int expAll, String countWhere) throws SQLException {
		if(diff.validateReturnCount) {
			Assert.assertEquals(expected, res);
		}else
			Assert.assertEquals(expAll, DalTestHelper.getCount(dao, countWhere));
	}

	public void assertEquals(int[] expected, int[] res, int expAll) throws SQLException {
		if(diff.validateReturnCount) {
			Assert.assertArrayEquals(expected, res);
		}else
			Assert.assertEquals(expAll, DalTestHelper.getCount(dao));
	}
	
	public void assertEquals(int[] expected, int[] res, int exp, String countWhere) throws SQLException {
		if(diff.validateReturnCount)
			Assert.assertArrayEquals(expected, res);
		else
			Assert.assertEquals(exp, DalTestHelper.getCount(dao, countWhere));
	}

	public void assertEqualsBatch(int[] expected, int[] res, int expAll) throws SQLException {
		if(diff.validateBatchUpdateCount) {
			Assert.assertArrayEquals(expected, res);
		}else
			Assert.assertEquals(expAll, DalTestHelper.getCount(dao));
	}
	
	public void assertEqualsBatch(int[] expected, int[] res, int expAll, String countWhere) throws SQLException {
		if(diff.validateBatchUpdateCount) {
			Assert.assertArrayEquals(expected, res);
		}else
			Assert.assertEquals(expAll, DalTestHelper.getCount(dao, countWhere));
	}
	
	public void assertEqualsBatchInsert(int[] expected, int[] res, int expAll) throws SQLException {
		if(diff.validateBatchInsertCount) {
			Assert.assertArrayEquals(expected, res);
		}else
			Assert.assertEquals(expAll, DalTestHelper.getCount(dao));
	}
}
