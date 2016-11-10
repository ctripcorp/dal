package com.ctrip.platform.dal.dao.unittests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import com.google.common.collect.Sets;

/**
 * This test case is too complex, we need to refactor it to make it focus on more "unit" testable
 * @author jhhe
 *
 */
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
	
	private void assertSelector(DatabaseSelector selector, ErrorCode code) {
		try {
			selector.select();
			Assert.fail();
		} catch (DalException e) {
			Assert.assertEquals(code.getCode(), e.getErrorCode());
		}
	}
	
	private String assertSelector(DatabaseSelector selector, String... matched) {
		try {
			String selected = selector.select();
			Assert.assertNotNull(selected);
				
			for(String v: matched)
				if(selected.equals(v))
					return selected;
			Assert.fail();
		} catch (DalException e) {
			e.printStackTrace();
			Assert.fail();
		}
		return null;
	}
	
	private String assertSelector(DatabaseSelector selector, Set<String> matched) {
		try {
			String selected = selector.select();
			Assert.assertNotNull(selected);
				
				if(matched.contains(selected)) {
					matched.remove(selected);
					return selected;
				}
			Assert.fail();
		} catch (DalException e) {
			e.printStackTrace();
			Assert.fail();
		}
		return null;
	}
	
	@Test
	public void validDesigantedDbTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, false, true);
		Assert.assertEquals(M1, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, false, true);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, false, true);
		Assert.assertEquals(S1, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), ms, ss, false, true);
		Assert.assertEquals(S2, selector.select());

		selector = new DatabaseSelector(new DalHints().inDatabase(S3), ms, ss, false, true);
		Assert.assertEquals(S3, selector.select());

		autoMarkdown(M1);
		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, false, true);
		Assert.assertEquals(M2, selector.select());
		
		autoMarkdown(S1);
		autoMarkdown(S2);
		selector = new DatabaseSelector(new DalHints().inDatabase(S3), ms, ss, false, true);
		Assert.assertEquals(S3, selector.select());
	}

	@Test
	public void invalidDesigantedDbTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1 + M2), ms, ss, false, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);

		selector = new DatabaseSelector(new DalHints().inDatabase(M1 + M2), ms, ss, true, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);

		selector = new DatabaseSelector(new DalHints().inDatabase(M1 + M2), ms, ss, true, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1 + M2), ms, ss, false, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void desigantedMarkdownDbTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		autoMarkdown(M1);
		autoMarkdown(M2);
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(new DalHints().inDatabase(S2), ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}
	
	@Test
	public void desigantedUsedInHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1)).inDatabase(M1), ms, ss, false, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(S1), ms, ss, false, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
	}
	
	@Test
	public void masterOnlyTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		// make sure no slave is qualified
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		selector = new DatabaseSelector(null, ms, ss, true, true);
		assertSelector(selector, M1, M2);

		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, M1, M2);

		autoMarkdown(M1);
		selector = new DatabaseSelector(null, ms, ss, true, true);
		assertSelector(selector, M2);
		
		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, M2);

		autoMarkdown(M2);
		selector = new DatabaseSelector(null, ms, ss, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}
	
	@Test
	public void masterOnlyHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		// make sure no slave is qualified
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(selector, M2);

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(selector, M1);

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, false);
		assertSelector(selector, M2);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, false);
		assertSelector(selector, M1);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1).addDB(M2)), ms, ss, true, false);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
		
		autoMarkdown(M1);
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(selector, M2);

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
		autoMarkup(M1);
		
		autoMarkdown(M2);
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(selector, M1);

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
		
		autoMarkdown(M1);
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M1).addDB(M2)), ms, ss, true, false);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
	}
	
	@Test
	public void notMasterOnlyTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, S1, S2, S3);

		selector = new DatabaseSelector(null, ms, ss, false, false);
		assertSelector(selector, M1, M2);
	}
	
	@Test
	public void notMasterOnlyMarkdownTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, M1, M2);
		
		selector = new DatabaseSelector(null, ms, ss, false, false);
		assertSelector(selector, M1, M2);

		setUp();
		
		autoMarkdown(M1);
		autoMarkdown(M2);
		
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, S1, S2, S3);
		
		selector = new DatabaseSelector(null, ms, ss, false, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		autoMarkdown(M1);
		autoMarkdown(M2);
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(null, ms, ss, false, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}
	
	@Test
	public void notMasterOnlyHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		Set<String> matched = Sets.newHashSet(S1, S2, S3);
		
		DalHints hints = new DalHints().setHA(new DalHA());
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);

		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		matched = Sets.newHashSet(M1, M2);
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);

		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
	}
	
	@Test
	public void notMasterOnlyHaMarkdownTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		Set<String> matched;
		
		autoMarkdown(S1);
		matched = Sets.newHashSet(S2, S3);
		DalHints hints = new DalHints().setHA(new DalHA());
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);

		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		autoMarkdown(M1);
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, M2);

		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
	}

	@Test
	public void isSelectTest() throws DalException {
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
		assertSelector(selector, M2);
		
		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		dbName = selector.select();
		assertSelector(selector, M2, M1);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, true, true);
		assertSelector(selector, M1);

		selector = new DatabaseSelector(new DalHints().inDatabase(M2), ms, ss, true, true);
		assertSelector(selector, M2);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, true, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void notSelectTest() throws DalException {
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
	public void onlyHaveMasterTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = null;
		DatabaseSelector selector;
		
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, M1, M2);

		selector = new DatabaseSelector(null, ms, ss, false, false);
		assertSelector(selector, M1, M2);
		
		// Master only
		selector = new DatabaseSelector(null, ms, ss, true, true);
		assertSelector(selector, M1, M2);

		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, M1, M2);
		
		autoMarkdown(M1);
		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, M2);
		
		autoMarkdown(M2);
		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}

	@Test
	public void onlyHaveMasterHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = null;
		DatabaseSelector selector;
		
		Set<String> matched;
		
		matched = Sets.newHashSet(M1, M2);
		DalHints hints = new DalHints().setHA(new DalHA());
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);

		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		// reset
		matched = Sets.newHashSet(M1, M2);
		hints = new DalHints().setHA(new DalHA());
		selector = new DatabaseSelector(hints, ms, ss, false, false);
		assertSelector(selector, matched);

		selector = new DatabaseSelector(hints, ms, ss, false, false);
		assertSelector(selector, matched);
		
		// Master only
		matched = Sets.newHashSet(M1, M2);
		hints = new DalHints().setHA(new DalHA());
		selector = new DatabaseSelector(hints, ms, ss, true, true);
		assertSelector(selector, M1, M2);

		selector = new DatabaseSelector(hints, ms, ss, true, true);
		assertSelector(selector, M1, M2);

		// reset
		matched = Sets.newHashSet(M1, M2);
		hints = new DalHints().setHA(new DalHA());
		selector = new DatabaseSelector(hints, ms, ss, true, false);
		assertSelector(selector, M1, M2);
		
		selector = new DatabaseSelector(hints, ms, ss, true, false);
		assertSelector(selector, M1, M2);
	}
	
	@Test
	public void onlyHaveMasterHaMarkdownTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = null;
		DatabaseSelector selector;
		
		// with markdown
		autoMarkdown(M1);
		DalHints hints = new DalHints().setHA(new DalHA());
		selector = new DatabaseSelector(hints, ms, ss, true, false);
		assertSelector(selector, M2);
		
		selector = new DatabaseSelector(hints, ms, ss, true, false);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
		
		// reset
		hints = new DalHints().setHA(new DalHA());
		autoMarkdown(M2);
		selector = new DatabaseSelector(hints, ms, ss, true, false);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
	}

	@Test
	public void onlyHaveSlaveTest() throws DalException {
		List<DataBase> ms = null;
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		selector = new DatabaseSelector(null, ms, ss, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		selector = new DatabaseSelector(null, ms, ss, false, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, S1, S2, S3);

		autoMarkdown(S1);
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, S2, S3);
		
		autoMarkdown(S2);
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, S3);
		
		autoMarkdown(S3);
		selector = new DatabaseSelector(null, ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}

	@Test
	public void onlyHaveSlaveHaTest() throws DalException {
		List<DataBase> ms = null;
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		Set<String> matched;
		matched = Sets.newHashSet(S1, S2, S3);

		DalHints hints = new DalHints().setHA(new DalHA());

		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
	}

	@Test
	public void onlyHaveSlaveHaMarkdownTest() throws DalException {
		List<DataBase> ms = null;
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		DatabaseSelector selector;
		
		Set<String> matched;

		autoMarkdown(S1);
		DalHints hints = new DalHints().setHA(new DalHA());
		matched = Sets.newHashSet(S2, S3);

		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, matched);
		
		selector = new DatabaseSelector(hints, ms, ss, false, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
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

	// The rest tests are all original tests, that I don't think well organized

	@Test
	public void onlyOneMasterMarkdownTest() {
		autoMarkdown(M1);
		DataBase db = new DataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(null, dbs, null, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		// masterOnly
		selector = new DatabaseSelector(null, dbs, null, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(null, dbs, null, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
	}
	
	@Test
	public void hasOneMarkdownMasterTest() throws DalException {
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DataBase(M1, true, "", M1));
		dbs.add(new DataBase(M2, true, "", M2));

		autoMarkdown(M1);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, false);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(null, dbs, null, false, true);
		Assert.assertEquals(M2, selector.select());

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
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(M2)), dbs, null, true, false);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
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
	public void onlyOneSlaveMarkdownTest() {
		DataBase db = new DataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);

		autoMarkdown(S1);
		DatabaseSelector selector = new DatabaseSelector(new DalHints(), null, dbs,
				false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs,
				false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(new DalHints().inDatabase(S2), null, dbs,
				false, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
		
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
		assertSelector(selector, ErrorCode.MarkdownConnection);

		// masterOnly
		selector = new DatabaseSelector(null, null, dbs, true, true);
		assertSelector(selector, ErrorCode.NullLogicDbName);

		selector = new DatabaseSelector(null, null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), null, dbs, true, false);
		assertSelector(selector, ErrorCode.NullLogicDbName);
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
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
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

		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false, true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));

		selector = new DatabaseSelector(new DalHints().inDatabase(M1), ms, ss, false, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, false, true);
		Assert.assertEquals(S1, selector.select());
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S2), ms, ss, false, true);
		Assert.assertEquals(S2, selector.select());
		
		// masterOnly
		selector = new DatabaseSelector(null, ms, ss, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);

		selector = new DatabaseSelector(null, ms, ss, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, true, true);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);

		selector = new DatabaseSelector(new DalHints().inDatabase(S1), ms, ss, true, false);
		assertSelector(selector, ErrorCode.InvalidDatabaseKeyName);
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

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(S1), ms, ss, false, true);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
		
		// masterOnly
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, true, true);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(M1), ms, ss, true, true);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, true, false);
		Assert.assertEquals(M2, selector.select());

		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(M1), ms, ss, true, false);
		assertSelector(selector, ErrorCode.MarkdownConnection);
		
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, false);
		Assert.assertEquals(M2, selector.select());

		autoMarkdown(M2);
		selector = new DatabaseSelector(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, false);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
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
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);


		ha = new DalHA();
		ha.addDB(M1);
		autoMarkdown(M2);
		selector = new DatabaseSelector(new DalHints().setHA(ha), ms, ss, false, false);
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);
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
		assertSelector(selector, ErrorCode.NoMoreConnectionToFailOver);

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
