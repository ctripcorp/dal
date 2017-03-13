package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import org.junit.Assert;

import test.com.ctrip.platform.dal.dao.unittests.DalTestHelper;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalTableDao;

public class BaseTestStub {
	public static class DatabaseDifference {
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
	public final static String SP_I_NAME = "dal_client_test_i";
	public final static String SP_D_NAME="dal_client_test_d";
	public final static String SP_U_NAME = "dal_client_test_u";
	public final static String SP_NO_OUT_NAME = "dal_client_test_no_out";
	public final static String MULTIPLE_RESULT_SP_SQL = "MULTIPLE_RESULT_SP_SQL";

	public DatabaseDifference diff;

	public DalClient client = null;
	public ClientTestDalRowMapper mapper = null;
	
	public DalTableDao<ClientTestModel> dao = null;
	
	public BaseTestStub(
			String dbName, 
			DatabaseDifference diff) {
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
