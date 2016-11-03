package com.ctrip.platform.dal.dao.unittests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.configure.DataBase;
import com.ctrip.platform.dal.dao.configure.DatabaseSelector;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkDownPolicy;
import com.ctrip.platform.dal.dao.markdown.MarkDownReason;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;
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
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
	}

	private void autoMarkdown(String key) {
		MarkDownInfo info = new MarkDownInfo(key, "1", MarkDownPolicy.TIMEOUT, 0);
		info.setReason(MarkDownReason.ERRORCOUNT);
		
		MarkdownManager.autoMarkdown(info);
	}
	private void autoMarkup(String key) {
		MarkdownManager.autoMarkup(new MarkupInfo(key, "", 1));
	}
	
	@Before
	public void setUp() {
		autoMarkup(M1);
		autoMarkup(M2);
		autoMarkup(S1);
		autoMarkup(S2);
		autoMarkup(S3);
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
		
		// Test chose database key name
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), dbs, null, true, true);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), dbs, null, true, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
	}
	
	private void assertSelector(DatabaseSelector selector, ErrorCode code) {
		try {
			selector.select();
			Assert.fail();
		} catch (DalException e) {
			Assert.assertEquals(code.getCode(), e.getErrorCode());
		}
	}


	@Test
	public void hasMarkdownMasterTest() {
		autoMarkdown(M1);
		DataBase db = new DataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		// masterOnly
		selector = new DatabaseSelector(null, dbs, null, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(null, dbs, null, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		// masterOnly with db pointed
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), dbs, null, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(new DalHints().inDatabase(M1), dbs, null, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		// not exist
		selector = new DatabaseSelector(new DalHints().inDatabase(M2), dbs, null, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), dbs, null, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}

	@Test
	public void hasOneMarkdownMasterTest() throws DalException {
		autoMarkdown(M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DataBase(M1, true, "", M1));
		dbs.add(new DataBase(M2, true, "", M2));

		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, false);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), dbs, null, false, false);
		Assert.assertEquals(M2, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), dbs, null, false, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);

		// masterOnly
		selector = new DatabaseSelector(null, dbs, null, true, false);
		Assert.assertEquals(M2, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M2), dbs, null, true, false);
		Assert.assertEquals(M2, selector.select());

		
		selector = new DatabaseSelector(null, dbs, null, true, true);
		Assert.assertEquals(M2, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M2), dbs, null, true, true);
		Assert.assertEquals(M2, selector.select());
		
		// test pointed db
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), dbs, null, true, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void onlyOneSlaveTest() throws DalException {
		DataBase db = new DataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, null, dbs,
				false, true);
		Assert.assertEquals(S1, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs, 
				false, true);
		Assert.assertEquals(S1, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(M1), null, dbs, false, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);

		// masterOnly
		selector = new DatabaseSelector(null, null, dbs, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		selector = new DatabaseSelector(null, null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		// test pointed
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasMarkdownSlaveTest() {
		autoMarkdown(S1);
		DataBase db = new DataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(new DalHints(), null, dbs,
				false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs,
				false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(new DalHints().inDatabase(S2), null, dbs,
				false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		// masterOnly
		selector = new DatabaseSelector(null, null, dbs, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), null, dbs, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		selector = new DatabaseSelector(null, null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasOneMarkdownSlaveTest() throws DalException {
		autoMarkdown(S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DataBase(S1, false, "", S1));
		dbs.add(new DataBase(S2, false, "", S2));

		DatabaseSelector selector = new DatabaseSelector(new DalHints(), null, dbs,
				false, true);
		Assert.assertEquals(S2, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), null, dbs,
				false, true);
		Assert.assertEquals(S2, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs,
				false, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);

		// masterOnly
		selector = new DatabaseSelector(null, null, dbs, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		selector = new DatabaseSelector(null, null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasMixedMasterAndSlaveButIsSelectTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false, true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, false, true);
		Assert.assertTrue(selector.select().equals(S1));

		selector = new DatabaseSelector(new DalHints().inDatabase(S2), ms, ss, false, true);
		Assert.assertTrue(selector.select().equals(S2));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, false, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
		
		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		dbName = selector.select();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, true, true);
		Assert.assertTrue(selector.select().equals(M1));

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, true, true);
		Assert.assertTrue(selector.select().equals(M2));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, true, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void hasMixedMasterAndSlaveButIsNoSelectTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DatabaseSelector selector = new DatabaseSelector(new DalHints(), ms, ss, false, false);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));

		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, false, false);
		Assert.assertTrue(selector.select().equals(M1));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, false, false);
		Assert.assertTrue(selector.select().equals(M2));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), ms, ss, false, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
		
		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, false);
		dbName = selector.select();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, true, false);
		Assert.assertTrue(selector.select().equals(M1));

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, true, false);
		Assert.assertTrue(selector.select().equals(M2));
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), ms, ss, true, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void hasMixedMasterAndSlaveButSlaveMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		autoMarkdown(S1);
		autoMarkdown(S2);

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false, true);
		Assert.assertEquals(M1, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, false, true);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, false, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
		
		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		Assert.assertEquals(M1, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, true, true);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(null, ms, ss, true, false);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, true, false);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(null, ms, ss, false, false);
		Assert.assertEquals(M1, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, false, false);
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

		autoMarkdown(M1);

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false,
				true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));

		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(null, ms, ss, false, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
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

		autoMarkdown(M1);

		DatabaseSelector selector = new DatabaseSelector(new DalHints(), ms, ss, false,
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

		autoMarkdown(M1);
		
		DatabaseSelector selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, true);
		Assert.assertEquals(S2, selector.select());

		// masterOnly
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, true, true);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, true, false);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, false);
		Assert.assertEquals(M2, selector.select());

		autoMarkdown(M2);
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, false);
		assertSelector(selector, ErrorCode.NoAvailableDatabase);
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

		autoMarkdown(S1);
		DalHA ha = new DalHA();
		ha.addDB(M1);

		DatabaseSelector selector = new DatabaseSelector(new DalHints().setHA(ha), ms, ss, false,
				true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S2));

		// masterOnly
		ha = new DalHA();
		ha.addDB(M1);
		selector = new DatabaseSelector(new DalHints().setHA(ha), ms, ss, true, true);
		Assert.assertEquals(M2, selector.select());

		ha = new DalHA();
		ha.addDB(M1);
		selector = new DatabaseSelector(new DalHints().setHA(ha), ms, ss, true, false);
		Assert.assertEquals(M2, selector.select());

		ha = new DalHA();
		ha.addDB(M1);
		selector = new DatabaseSelector(new DalHints().setHA(ha), ms, ss, false, false);
		Assert.assertEquals(M2, selector.select());
		
		ha = new DalHA();
		ha.addDB(M1);
		selector = new DatabaseSelector(new DalHints().setHA(ha).inDatabase(M1), ms, ss, false, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);


		ha = new DalHA();
		ha.addDB(M1);
		autoMarkdown(M2);
		selector = new DatabaseSelector(new DalHints().setHA(ha), ms, ss, false, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}

	@Test
	public void hasHASlavesTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		ha.addDB(S1);

		DatabaseSelector selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, false,
				true);
		Assert.assertEquals(S2, selector.select());

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasHASlavesAllNotUsedTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);

		DatabaseSelector selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);


		ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasHASlavesOneMarkdownTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		autoMarkdown(S2);

		DatabaseSelector selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, false,
				true);
		Assert.assertEquals(S1, selector.select());

		// masterOnly
		ha = new DalHA();
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		ha = new DalHA();
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasHASlavesOneMarkdownFailOverTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));

		DalHA ha = new DalHA();
		ha.addDB(S1);
		autoMarkdown(S2);

		DatabaseSelector selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, false,
				true);
		Assert.assertEquals(S3, selector.select());

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		ha = new DalHA();
		ha.addDB(S1);
		selector = new DatabaseSelector(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
	}
}
