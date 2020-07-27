package com.ctrip.platform.dal.dao.unittests;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.configure.DataBase;
import com.ctrip.platform.dal.dao.configure.DefaultDataBase;
import com.ctrip.platform.dal.dao.configure.DefaultDatabaseSelector;
import com.ctrip.platform.dal.dao.configure.SelectionContext;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.MockEnvUtils;
import com.ctrip.platform.dal.dao.markdown.*;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This test case is too complex, we need to refactor it to make it focus on more "unit" testable
 * @author jhhe
 *
 */
public class DatabaseSelectorTest {

	private static final String M1 = "MySqlShard_0";
	private static final String M2 = "MySqlShard_1";
	private static final String S1 = "OracleShard_0";
	private static final String S2 = "dao_test_sqlsvr_0";
	private static final String S3 = "dao_test_sqlsvr_1";
	private static final MockEnvUtils ENV_UTILS = new MockEnvUtils();
	private DefaultDatabaseSelector selector = new DefaultDatabaseSelector(ENV_UTILS);
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
	
	private void assertSelector(SelectionContext context, ErrorCode code) {
		try {
			selector.select(context);
			Assert.fail();
		} catch (DalException e) {
			Assert.assertEquals(code.getCode(), e.getErrorCode());
		}
	}
	
	private String assertSelector(SelectionContext context, String... matched) {
		try {
			String selected = selector.select(context).getConnectionString();
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
	
	private String assertSelector(SelectionContext context, Set<String> matched) {
		try {
			String selected = selector.select(context).getConnectionString();
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
	
	private SelectionContext getContext(DalHints hints, List<DataBase> ms, List<DataBase> ss, boolean masterOnly, boolean isSelect) {
	    SelectionContext ctx = new SelectionContext("", hints == null ? new DalHints():hints, null, masterOnly, isSelect);
	    ctx.setMasters(ms);
	    ctx.setSlaves(ss);
	    return ctx;
	}
	
	@Test
	public void validDesigantedDbTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
        SelectionContext context;
        
		context = getContext(new DalHints().inDatabase(M1), ms, ss, false, true);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		context = getContext(new DalHints().inDatabase(M2), ms, ss, false, true);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		context = getContext(new DalHints().inDatabase(S1), ms, ss, false, true);
		Assert.assertEquals(S1, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(S2), ms, ss, false, true);
		Assert.assertEquals(S2, selector.select(context).getConnectionString());

		context = getContext(new DalHints().inDatabase(S3), ms, ss, false, true);
		Assert.assertEquals(S3, selector.select(context).getConnectionString());

		autoMarkdown(M1);
		context = getContext(new DalHints().inDatabase(M2), ms, ss, false, true);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());
		
		autoMarkdown(S1);
		autoMarkdown(S2);
		context = getContext(new DalHints().inDatabase(S3), ms, ss, false, true);
		Assert.assertEquals(S3, selector.select(context).getConnectionString());
	}

	@Test
	public void invalidDesigantedDbTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		context = getContext(new DalHints().inDatabase(M1 + M2), ms, ss, false, false);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);

		context = getContext(new DalHints().inDatabase(M1 + M2), ms, ss, true, true);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);

		context = getContext(new DalHints().inDatabase(M1 + M2), ms, ss, true, false);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);
		
		context = getContext(new DalHints().inDatabase(M1 + M2), ms, ss, false, true);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void desigantedMarkdownDbTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		autoMarkdown(M1);
		autoMarkdown(M2);
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		context = getContext(new DalHints().inDatabase(M1), ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(new DalHints().inDatabase(M2), ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(new DalHints().inDatabase(S1), ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(new DalHints().inDatabase(S2), ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(new DalHints().inDatabase(S2), ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
	}
	
	@Test
	public void desigantedUsedInHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(M1)).inDatabase(M1), ms, ss, false, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(S1), ms, ss, false, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
	}
	
	@Test
	public void masterOnlyTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		// make sure no slave is qualified
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		context = getContext(null, ms, ss, true, true);
		assertSelector(context, M1, M2);

		context = getContext(null, ms, ss, true, false);
		assertSelector(context, M1, M2);

		autoMarkdown(M1);
		context = getContext(null, ms, ss, true, true);
		assertSelector(context, M2);
		
		context = getContext(null, ms, ss, true, false);
		assertSelector(context, M2);

		autoMarkdown(M2);
		context = getContext(null, ms, ss, true, true);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(null, ms, ss, true, false);
		assertSelector(context, ErrorCode.MarkdownConnection);
	}
	
	@Test
	public void masterOnlyHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		// make sure no slave is qualified
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(context, M2);

		context = getContext(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(context, M1);

		context = getContext(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, false);
		assertSelector(context, M2);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, false);
		assertSelector(context, M1);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(M1).addDB(M2)), ms, ss, true, false);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
		
		autoMarkdown(M1);
		context = getContext(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(context, M2);

		context = getContext(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(context, M2);
		autoMarkup(M1);
		
		autoMarkdown(M2);
		context = getContext(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(context, M1);

		context = getContext(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(context, M1);
		
		autoMarkdown(M1);
		context = getContext(new DalHints().setHA(new DalHA().addDB(M1)), ms, ss, true, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);

		context = getContext(new DalHints().setHA(new DalHA().addDB(M2)), ms, ss, true, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(M1).addDB(M2)), ms, ss, true, false);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
	}
	
	@Test
	public void notMasterOnlyTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, S1, S2, S3);

		context = getContext(null, ms, ss, false, false);
		assertSelector(context, M1, M2);
	}
	
	@Test
	public void notMasterOnlyMarkdownTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, M1, M2);
		
		context = getContext(null, ms, ss, false, false);
		assertSelector(context, M1, M2);

		setUp();
		
		autoMarkdown(M1);
		autoMarkdown(M2);
		
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, S1, S2, S3);
		
		context = getContext(null, ms, ss, false, false);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		autoMarkdown(M1);
		autoMarkdown(M2);
		autoMarkdown(S1);
		autoMarkdown(S2);
		autoMarkdown(S3);
		
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(null, ms, ss, false, false);
		assertSelector(context, ErrorCode.MarkdownConnection);
	}
	
	private Set<String> newHashSet(String... v) {
		Set<String> hset = new HashSet<>(); 	
		for(String s: v){
			hset.add(s);
		}
		
		return hset;
	}
	
	@Test
	public void notMasterOnlyHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		Set<String> matched = newHashSet(S1, S2, S3);
		
		DalHints hints = new DalHints().setHA(new DalHA());
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);

		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		matched = newHashSet(M1, M2);
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);

		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
	}
	
	@Test
	public void notMasterOnlyHaMarkdownTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		Set<String> matched;
		
		autoMarkdown(S1);
		matched = newHashSet(S2, S3);
		DalHints hints = new DalHints().setHA(new DalHA());
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);

		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		autoMarkdown(M1);
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, M2);

		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, M2);
	}

	@Test
	public void isSelectTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
        SelectionContext context;

		context = getContext(null, ms, ss, false, true);
		String dbName = selector.select(context).getConnectionString();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));
		
		context = getContext(new DalHints().inDatabase(S1), ms, ss, false, true);
		Assert.assertTrue(selector.select(context).getConnectionString().equals(S1));

		context = getContext(new DalHints().inDatabase(S2), ms, ss, false, true);
		Assert.assertTrue(selector.select(context).getConnectionString().equals(S2));
		
		context = getContext(new DalHints().inDatabase(M2), ms, ss, false, true);
		assertSelector(context, M2);
		
		// masterOnly
		context = getContext(null, ms, ss, true, true);
		dbName = selector.select(context).getConnectionString();
		assertSelector(context, M2, M1);
		
		context = getContext(new DalHints().inDatabase(M1), ms, ss, true, true);
		assertSelector(context, M1);

		context = getContext(new DalHints().inDatabase(M2), ms, ss, true, true);
		assertSelector(context, M2);
		
		context = getContext(new DalHints().inDatabase(S1), ms, ss, true, true);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void notSelectTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
        SelectionContext context;

		context = getContext(new DalHints(), ms, ss, false, false);
		String dbName = selector.select(context).getConnectionString();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));

		context = getContext(new DalHints().inDatabase(M1), ms, ss, false, false);
		Assert.assertTrue(selector.select(context).getConnectionString().equals(M1));
		
		context = getContext(new DalHints().inDatabase(M2), ms, ss, false, false);
		Assert.assertTrue(selector.select(context).getConnectionString().equals(M2));
		
		context = getContext(new DalHints().inDatabase(S2), ms, ss, false, false);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);
		
		// masterOnly
		context = getContext(null, ms, ss, true, false);
		dbName = selector.select(context).getConnectionString();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));
		
		context = getContext(new DalHints().inDatabase(M1), ms, ss, true, false);
		Assert.assertTrue(selector.select(context).getConnectionString().equals(M1));

		context = getContext(new DalHints().inDatabase(M2), ms, ss, true, false);
		Assert.assertTrue(selector.select(context).getConnectionString().equals(M2));
		
		context = getContext(new DalHints().inDatabase(S2), ms, ss, true, false);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);
	}
	
	@Test
	public void onlyHaveMasterTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = null;
		SelectionContext context;
		
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, M1, M2);

		context = getContext(null, ms, ss, false, false);
		assertSelector(context, M1, M2);
		
		// Master only
		context = getContext(null, ms, ss, true, true);
		assertSelector(context, M1, M2);

		context = getContext(null, ms, ss, true, false);
		assertSelector(context, M1, M2);
		
		autoMarkdown(M1);
		context = getContext(null, ms, ss, true, false);
		assertSelector(context, M2);
		
		autoMarkdown(M2);
		context = getContext(null, ms, ss, true, false);
		assertSelector(context, ErrorCode.MarkdownConnection);
	}

	@Test
	public void onlyHaveMasterHaTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = null;
		SelectionContext context;
		
		Set<String> matched;
		
		matched = newHashSet(M1, M2);
		DalHints hints = new DalHints().setHA(new DalHA());
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);

		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		// reset
		matched = newHashSet(M1, M2);
		hints = new DalHints().setHA(new DalHA());
		context = getContext(hints, ms, ss, false, false);
		assertSelector(context, matched);

		context = getContext(hints, ms, ss, false, false);
		assertSelector(context, matched);
		
		// Master only
		matched = newHashSet(M1, M2);
		hints = new DalHints().setHA(new DalHA());
		context = getContext(hints, ms, ss, true, true);
		assertSelector(context, M1, M2);

		context = getContext(hints, ms, ss, true, true);
		assertSelector(context, M1, M2);

		// reset
		matched = newHashSet(M1, M2);
		hints = new DalHints().setHA(new DalHA());
		context = getContext(hints, ms, ss, true, false);
		assertSelector(context, M1, M2);
		
		context = getContext(hints, ms, ss, true, false);
		assertSelector(context, M1, M2);
	}
	
	@Test
	public void onlyHaveMasterHaMarkdownTest() throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = null;
		SelectionContext context;
		
		// with markdown
		autoMarkdown(M1);
		DalHints hints = new DalHints().setHA(new DalHA());
		context = getContext(hints, ms, ss, true, false);
		assertSelector(context, M2);
		
		context = getContext(hints, ms, ss, true, false);
		assertSelector(context, M2);
		
		// reset
		hints = new DalHints().setHA(new DalHA());
		autoMarkdown(M2);
		context = getContext(hints, ms, ss, true, false);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
	}

	@Test
	public void onlyHaveSlaveTest() throws DalException {
		List<DataBase> ms = null;
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		context = getContext(null, ms, ss, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
		
		context = getContext(null, ms, ss, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);
		
		context = getContext(null, ms, ss, false, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
		
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, S1, S2, S3);

		autoMarkdown(S1);
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, S2, S3);
		
		autoMarkdown(S2);
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, S3);
		
		autoMarkdown(S3);
		context = getContext(null, ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
	}

	@Test
	public void onlyHaveSlaveHaTest() throws DalException {
		List<DataBase> ms = null;
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		Set<String> matched;
		matched = newHashSet(S1, S2, S3);

		DalHints hints = new DalHints().setHA(new DalHA());

		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
	}

	@Test
	public void onlyHaveSlaveHaMarkdownTest() throws DalException {
		List<DataBase> ms = null;
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));
		SelectionContext context;
		
		Set<String> matched;

		autoMarkdown(S1);
		DalHints hints = new DalHints().setHA(new DalHA());
		matched = newHashSet(S2, S3);

		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, matched);
		
		context = getContext(hints, ms, ss, false, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
	}

	@Test
	public void onlyOneMasterTest() throws DalException {
		DataBase db = new DefaultDataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);

		SelectionContext context = getContext(null, dbs, null,
				false, false);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		context = getContext(null, dbs, null, false, true);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		// masterOnly
		context = getContext(null, dbs, null, true, false);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		context = getContext(null, dbs, null, true, true);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());
	}

	// The rest tests are all original tests, that I don't think well organized

	@Test
	public void onlyOneMasterMarkdownTest() {
		autoMarkdown(M1);
		DataBase db = new DefaultDataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		SelectionContext context = getContext(null, dbs, null, false, false);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(null, dbs, null, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		// masterOnly
		context = getContext(null, dbs, null, true, false);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(null, dbs, null, true, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
	}
	
	@Test
	public void hasOneMarkdownMasterTest() throws DalException {
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DefaultDataBase(M1, true, "", M1));
		dbs.add(new DefaultDataBase(M2, true, "", M2));

		autoMarkdown(M1);
		SelectionContext context = getContext(null, dbs, null, false, false);
		assertSelector(context, M2);

		context = getContext(null, dbs, null, false, true);
		assertSelector(context, M2);

		// masterOnly
		context = getContext(null, dbs, null, true, false);
		assertSelector(context, M2);
		
		context = getContext(new DalHints().inDatabase(M2), dbs, null, true, false);
		assertSelector(context, M2);
		
		context = getContext(null, dbs, null, true, true);
		assertSelector(context, M2);
		
		context = getContext(new DalHints().inDatabase(M2), dbs, null, true, true);
		assertSelector(context, M2);
		
		// test pointed db
		context = getContext(new DalHints().inDatabase(M1), dbs, null, true, false);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(M2)), dbs, null, true, false);
		assertSelector(context, M2);
	}

	@Test
	public void onlyOneSlaveTest() throws DalException {
		DataBase db = new DefaultDataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		SelectionContext context = getContext(null, null, dbs,
				false, true);
		Assert.assertEquals(S1, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(S1), null, dbs, 
				false, true);
		Assert.assertEquals(S1, selector.select(context).getConnectionString());

		context = getContext(new DalHints().inDatabase(M1), null, dbs, false, true);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);

		// masterOnly
		context = getContext(null, null, dbs, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);

		context = getContext(null, null, dbs, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
		
		// test pointed
		context = getContext(new DalHints().inDatabase(S1), null, dbs, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);

		context = getContext(new DalHints().inDatabase(S1), null, dbs, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
	}

	@Test
	public void onlyOneSlaveMarkdownTest() {
		DataBase db = new DefaultDataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);

		autoMarkdown(S1);
		SelectionContext context = getContext(new DalHints(), null, dbs,
				false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(new DalHints().inDatabase(S1), null, dbs,
				false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(new DalHints().inDatabase(S2), null, dbs,
				false, true);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);
		
		// masterOnly
		context = getContext(null, null, dbs, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);
		
		context = getContext(new DalHints().inDatabase(S2), null, dbs, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);

		context = getContext(null, null, dbs, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
		
		context = getContext(new DalHints().inDatabase(S2), null, dbs, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasOneMarkdownSlaveTest() throws DalException {
		autoMarkdown(S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DefaultDataBase(S1, false, "", S1));
		dbs.add(new DefaultDataBase(S2, false, "", S2));

		SelectionContext context = getContext(new DalHints(), null, dbs,
				false, true);
		Assert.assertEquals(S2, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(S2), null, dbs,
				false, true);
		Assert.assertEquals(S2, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(S1), null, dbs,
				false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);

		// masterOnly
		context = getContext(null, null, dbs, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);

		context = getContext(null, null, dbs, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
		
		context = getContext(new DalHints().inDatabase(S1), null, dbs, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasMixedMasterAndSlaveButSlaveMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		autoMarkdown(S1);
		autoMarkdown(S2);

		SelectionContext context = getContext(null, ms, ss, false, true);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(M1), ms, ss, false, true);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		context = getContext(new DalHints().inDatabase(S1), ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		// masterOnly
		context = getContext(null, ms, ss, true, true);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(M1), ms, ss, true, true);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		context = getContext(null, ms, ss, true, false);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		context = getContext(new DalHints().inDatabase(M1), ms, ss, true, false);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());

		context = getContext(null, ms, ss, false, false);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(M1), ms, ss, false, false);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());
	}

	@Test
	public void hasMixedMasterAndSlaveButMasterMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		autoMarkdown(M1);

		SelectionContext context = getContext(null, ms, ss, false, true);
		String dbName = selector.select(context).getConnectionString();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));

		context = getContext(new DalHints().inDatabase(M1), ms, ss, false, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(new DalHints().inDatabase(S1), ms, ss, false, true);
		Assert.assertEquals(S1, selector.select(context).getConnectionString());
		
		context = getContext(new DalHints().inDatabase(S2), ms, ss, false, true);
		Assert.assertEquals(S2, selector.select(context).getConnectionString());
		
		// masterOnly
		context = getContext(null, ms, ss, true, true);
		assertSelector(context, ErrorCode.MarkdownConnection);

		context = getContext(null, ms, ss, true, false);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(new DalHints().inDatabase(S1), ms, ss, true, true);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);

		context = getContext(new DalHints().inDatabase(S1), ms, ss, true, false);
		assertSelector(context, ErrorCode.InvalidDatabaseKeyName);
	}

	@Test
	public void hasMixedMasterAndSlaveButOneMasterMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		autoMarkdown(M1);

		SelectionContext context = getContext(new DalHints(), ms, ss, false,
				true);
		String dbName = selector.select(context).getConnectionString();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));

		// masterOnly
		context = getContext(null, ms, ss, true, true);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		context = getContext(null, ms, ss, true, false);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		context = getContext(null, ms, ss, false, false);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());
	}

	@Test
	public void hasHAMixedMasterAndSlaveButOneMasterMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		autoMarkdown(M1);
		
		SelectionContext context = getContext(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, true);
		Assert.assertEquals(S2, selector.select(context).getConnectionString());

		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(S1), ms, ss, false, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
		
		// masterOnly
		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, true, true);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(M1), ms, ss, true, true);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, true, false);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)).inDatabase(M1), ms, ss, true, false);
		assertSelector(context, ErrorCode.MarkdownConnection);
		
		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, false);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		autoMarkdown(M2);
		context = getContext(new DalHints().setHA(new DalHA().addDB(S1)), ms, ss, false, false);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);
	}

	@Test
	public void hasHAMixedMasterAndSlaveButOneSlaveMarkdownTest()
			throws DalException {
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DefaultDataBase(M1, true, "", M1));
		ms.add(new DefaultDataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		autoMarkdown(S1);
		DalHA ha = new DalHA();
		ha.addDB(M1);

		SelectionContext context = getContext(new DalHints().setHA(ha), ms, ss, false,
				true);
		String dbName = selector.select(context).getConnectionString();
		Assert.assertTrue(dbName.equals(S2));

		// masterOnly
		ha = new DalHA();
		ha.addDB(M1);
		context = getContext(new DalHints().setHA(ha), ms, ss, true, true);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		ha = new DalHA();
		ha.addDB(M1);
		context = getContext(new DalHints().setHA(ha), ms, ss, true, false);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());

		ha = new DalHA();
		ha.addDB(M1);
		context = getContext(new DalHints().setHA(ha), ms, ss, false, false);
		Assert.assertEquals(M2, selector.select(context).getConnectionString());
		
		ha = new DalHA();
		ha.addDB(M1);
		context = getContext(new DalHints().setHA(ha).inDatabase(M1), ms, ss, false, false);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);


		ha = new DalHA();
		ha.addDB(M1);
		autoMarkdown(M2);
		context = getContext(new DalHints().setHA(ha), ms, ss, false, false);
		Assert.assertEquals(M1, selector.select(context).getConnectionString());
	}

	@Test
	public void hasHASlavesTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		ha.addDB(S1);

		SelectionContext context = getContext(new DalHints().setHA(ha), null, ss, false,
				true);
		Assert.assertEquals(S2, selector.select(context).getConnectionString());

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		context = getContext(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);

		ha = new DalHA();
		ha.addDB(S1);
		context = getContext(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasHASlavesAllNotUsedTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);

		SelectionContext context = getContext(new DalHints().setHA(ha), null, ss, false, true);
		assertSelector(context, ErrorCode.NoMoreConnectionToFailOver);

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);
		context = getContext(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);


		ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);
		context = getContext(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasHASlavesOneMarkdownTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));

		DalHA ha = new DalHA();
		autoMarkdown(S2);

		SelectionContext context = getContext(new DalHints().setHA(ha), null, ss, false,
				true);
		Assert.assertEquals(S1, selector.select(context).getConnectionString());

		// masterOnly
		ha = new DalHA();
		context = getContext(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);

		ha = new DalHA();
		context = getContext(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
	}

	@Test
	public void hasHASlavesOneMarkdownFailOverTest() throws DalException {
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DefaultDataBase(S1, false, "", S1));
		ss.add(new DefaultDataBase(S2, false, "", S2));
		ss.add(new DefaultDataBase(S3, false, "", S3));

		DalHA ha = new DalHA();
		ha.addDB(S1);
		autoMarkdown(S2);

		SelectionContext context = getContext(new DalHints().setHA(ha), null, ss, false,
				true);
		Assert.assertEquals(S3, selector.select(context).getConnectionString());

		// masterOnly
		ha = new DalHA();
		ha.addDB(S1);
		context = getContext(new DalHints().setHA(ha), null, ss, true, true);
		assertSelector(context, ErrorCode.NullLogicDbName);

		ha = new DalHA();
		ha.addDB(S1);
		context = getContext(new DalHints().setHA(ha), null, ss, true, false);
		assertSelector(context, ErrorCode.NullLogicDbName);
	}

	@Test
	public void testSlaveOnly() {
		ENV_UTILS.setEnv("pro");
		List<DataBase> dbs = new ArrayList<>();
		dbs.add(new DefaultDataBase("mock-master", true, "", "mock-master"));
		SelectionContext ctx = getContext(new DalHints().slaveOnly(), dbs, null, false, true);
		assertSelector(ctx, ErrorCode.NullLogicDbName);
		ENV_UTILS.setEnv("uat");
		assertSelector(ctx, "mock-master");
		ENV_UTILS.setEnv(null);
	}

}
