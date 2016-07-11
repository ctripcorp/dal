package com.ctrip.platform.dal.dao.unittests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.configure.DataBase;
import com.ctrip.platform.dal.dao.configure.DatabaseSelector;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DatabaseSelectorTest {

	private static final String M1 = "ha_test";
	private static final String M2 = "ha_test_1";
	private static final String S1 = "ha_test_2";
	private static final String S2 = "dao_test_sqlsvr_0";
	private static final String S3 = "dao_test_sqlsvr_1";

	static {
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
	}

	@Before
	public void setUp() {
		MarkdownManager.autoMarkup(M1);
		MarkdownManager.autoMarkup(M2);
		MarkdownManager.autoMarkup(S1);
		MarkdownManager.autoMarkup(S2);
		MarkdownManager.autoMarkup(S3);
	}

	@Test
	public void onlyOneMasterTest() throws DalException {
		DataBase db = new DataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null,
				false, false);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(null, dbs, null, false, true);
		Assert.assertEquals(M1, selector.select());

		// masterOnly
		selector = new DatabaseSelector(null, dbs, null, true, false);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(null, dbs, null, true, true);
		Assert.assertEquals(M1, selector.select());
	}

	@Test
	public void hasMarkdownMasterTest() {
		MarkdownManager.autoMarkdown(M1);
		DataBase db = new DataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null,
				false, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}

		// masterOnly
		selector = new DatabaseSelector(null, dbs, null, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}

		selector = new DatabaseSelector(null, dbs, null, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasOneMarkdownMasterTest() throws DalException {
		MarkdownManager.autoMarkdown(M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DataBase(M1, true, "", M1));
		dbs.add(new DataBase(M2, true, "", M2));

		DatabaseSelector selector = new DatabaseSelector(null, dbs, null,
				false, false);
		Assert.assertEquals(M2, selector.select());

		// masterOnly
		selector = new DatabaseSelector(null, dbs, null, true, false);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(null, dbs, null, true, true);
		Assert.assertEquals(M2, selector.select());
	}

	@Test
	public void onlyOneSlaveTest() throws DalException {
		DataBase db = new DataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null,
				false, true);
		Assert.assertEquals(S1, selector.select());

		// masterOnly
		selector = new DatabaseSelector(null, dbs, null, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}

		selector = new DatabaseSelector(null, dbs, null, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasMarkdownSlaveTest() {
		MarkdownManager.autoMarkdown(S1);
		DataBase db = new DataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, null, dbs,
				false, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}

		// masterOnly
		selector = new DatabaseSelector(null, null, dbs, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}

		selector = new DatabaseSelector(null, null, dbs, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasOneMarkdownSlaveTest() throws DalException {
		MarkdownManager.autoMarkdown(S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DataBase(S1, false, "", S1));
		dbs.add(new DataBase(S2, false, "", S2));

		DatabaseSelector selector = new DatabaseSelector(null, null, dbs,
				false, true);
		Assert.assertEquals(S2, selector.select());

		// masterOnly
		selector = new DatabaseSelector(null, null, dbs, false, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}

		selector = new DatabaseSelector(null, null, dbs, false, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasMixedMasterAndSlaveButIsSelectTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false,
				true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));

		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		dbName = selector.select();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));
	}

	@Test
	public void hasMixedMasterAndSlaveButIsNoSelectTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false,
				false);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));

		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, false);
		dbName = selector.select();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));
	}

	@Test
	public void hasMixedMasterAndSlaveButSlaveMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		MarkdownManager.autoMarkdown(S1);
		MarkdownManager.autoMarkdown(S2);

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false,
				true);
		Assert.assertEquals(M1, selector.select());

		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(null, ms, ss, true, false);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(null, ms, ss, false, false);
		Assert.assertEquals(M1, selector.select());
	}

	@Test
	public void hasMixedMasterAndSlaveButMasterMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		MarkdownManager.autoMarkdown(M1);

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false,
				true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));

		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}

		selector = new DatabaseSelector(null, ms, ss, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}

		selector = new DatabaseSelector(null, ms, ss, false, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasMixedMasterAndSlaveButOneMasterMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		MarkdownManager.autoMarkdown(M1);

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false,
				true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));

		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(null, ms, ss, true, false);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(null, ms, ss, false, false);
		Assert.assertEquals(M2, selector.select());
	}

	@Test
	public void hasHAMixedMasterAndSlaveButOneMasterMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		MarkdownManager.autoMarkdown(M1);
		DalHA ha = new DalHA();
		ha.addDB(S1);

		DatabaseSelector selector = new DatabaseSelector(ha, ms, ss, false,
				true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S2));

		// masterOnly
		selector = new DatabaseSelector(ha, ms, ss, true, true);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(ha, ms, ss, true, false);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(ha, ms, ss, false, false);
		Assert.assertEquals(M2, selector.select());

		MarkdownManager.autoMarkdown(M2);
		selector = new DatabaseSelector(ha, ms, ss, false, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasHAMixedMasterAndSlaveButOneSlaveMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		MarkdownManager.autoMarkdown(S1);
		DalHA ha = new DalHA();
		ha.addDB(M1);

		DatabaseSelector selector = new DatabaseSelector(ha, ms, ss, false,
				true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S2));

		// masterOnly
		ha = new DalHA();
		ha.addDB(M1);
		selector = new DatabaseSelector(ha, ms, ss, true, true);
		Assert.assertEquals(M2, selector.select());

		ha = new DalHA();
		ha.addDB(M1);
		selector = new DatabaseSelector(ha, ms, ss, true, false);
		Assert.assertEquals(M2, selector.select());

		ha = new DalHA();
		ha.addDB(M1);
		selector = new DatabaseSelector(ha, ms, ss, false, false);
		Assert.assertEquals(M2, selector.select());

		ha = new DalHA();
		ha.addDB(M1);
		MarkdownManager.autoMarkdown(M2);
		selector = new DatabaseSelector(ha, ms, ss, false, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasHASlavesTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		ha.addDB(S1);

		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false,
				true);
		Assert.assertEquals(S2, selector.select());

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(ha, null, ss, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}

		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(ha, null, ss, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasHASlavesAllNotUsedTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);

		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false,
				true);
		Assert.assertNull(selector.select());

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);
		selector = new DatabaseSelector(ha, null, ss, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}

		ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);
		selector = new DatabaseSelector(ha, null, ss, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasHASlavesOneMarkdownTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		MarkdownManager.autoMarkdown(S2);

		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false,
				true);
		Assert.assertEquals(S1, selector.select());

		// masterOnly
		ha = new DalHA();
		selector = new DatabaseSelector(ha, null, ss, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}

		ha = new DalHA();
		selector = new DatabaseSelector(ha, null, ss, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}
	}

	@Test
	public void hasHASlavesOneMarkdownFailOverTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));

		DalHA ha = new DalHA();
		ha.addDB(S1);
		MarkdownManager.autoMarkdown(S2);

		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false,
				true);
		Assert.assertEquals(S3, selector.select());

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(ha, null, ss, true, true);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}

		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(ha, null, ss, true, false);
		try {
			selector.select();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.NullLogicDbName.getCode(),
					e.getErrorCode());
		}
	}
}
