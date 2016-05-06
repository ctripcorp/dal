package com.ctrip.platform.dal.dao.unittests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.dao.configure.DataBase;
import com.ctrip.platform.dal.dao.configure.DatabaseSelector;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DatabaseSelectorTest {

	private static final String M1 = "M1";
	private static final String M2 = "M2";
	private static final String S1 = "S1";
	private static final String S2 = "S2";
	private static final String S3 = "S3";
	
	static{
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
	}
	
	@Before
	public void setUp(){
		ConfigBeanFactory.getMarkdownConfigBean().markup(M1);
		ConfigBeanFactory.getMarkdownConfigBean().markup(M2);
		ConfigBeanFactory.getMarkdownConfigBean().markup(S1);
		ConfigBeanFactory.getMarkdownConfigBean().markup(S2);
		ConfigBeanFactory.getMarkdownConfigBean().markup(S3);
	}
	
	@Test
	public void onlyOneMasterTest() throws DalException {
		DataBase db = new DataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, false);
		Assert.assertEquals(M1, selector.select());
	}

	@Test
	public void hasMarkdownMasterTest(){
		ConfigBeanFactory.getMarkdownConfigBean().markdown(M1);
		DataBase db = new DataBase(M1, true, "", M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, false);
		try{
			selector.select();
		}catch(DalException e){
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void hasOneMarkdownMasterTest() throws DalException{
		ConfigBeanFactory.getMarkdownConfigBean().markdown(M1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DataBase(M1, true, "", M1));
		dbs.add(new DataBase(M2, true, "", M2));
		
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, false);
		Assert.assertEquals(M2, selector.select());
	}
	
	@Test
	public void onlyOneSlaveTest() throws DalException{
		DataBase db = new DataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, dbs, null, false, true);
		Assert.assertEquals(S1, selector.select());
	}
	
	@Test
	public void hasMarkdownSlaveTest(){
		ConfigBeanFactory.getMarkdownConfigBean().markdown(S1);
		DataBase db = new DataBase(S1, false, "", S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(db);
		DatabaseSelector selector = new DatabaseSelector(null, null, dbs, false, true);
		try{
			selector.select();
		}catch(DalException e){
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void hasOneMarkdownSlaveTest() throws DalException{
		ConfigBeanFactory.getMarkdownConfigBean().markdown(S1);
		List<DataBase> dbs = new ArrayList<DataBase>();
		dbs.add(new DataBase(S1, false, "", S1));
		dbs.add(new DataBase(S2, false, "", S2));
		
		DatabaseSelector selector = new DatabaseSelector(null, null, dbs, false, true);
		Assert.assertEquals(S2, selector.select());
	}
	
	@Test
	public void hasMixedMasterAndSlaveButIsSelectTest() throws DalException{
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		
		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false, true);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(S1) || dbName.equals(S2));
	}
	
	@Test
	public void hasMixedMasterAndSlaveButIsNoSelectTest() throws DalException{
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		ms.add(new DataBase(M2, true, "", M2));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		
		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false, false);
		String dbName = selector.select();
		Assert.assertTrue(dbName.equals(M1) || dbName.equals(M2));
	}
	
	@Test
	public void hasMixedMasterAndSlaveButSlaveMarkdownTest() throws DalException{
		List<DataBase> ms = new ArrayList<DataBase>();
		ms.add(new DataBase(M1, true, "", M1));
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		
		ConfigBeanFactory.getMarkdownConfigBean().markdown(S1);
		ConfigBeanFactory.getMarkdownConfigBean().markdown(S2);
		
		DatabaseSelector selector = new DatabaseSelector(null, ms, ss, false, true);
		Assert.assertEquals(M1, selector.select());
	}
	
	@Test
	public void hasHASlavesTest() throws DalException{
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		
		DalHA ha = new DalHA();
		ha.addDB(S1);
		
		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false, true);
		Assert.assertEquals(S2, selector.select());
	}
	
	@Test
	public void hasHASlavesAllNotUsedTest() throws DalException{
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		
		DalHA ha = new DalHA();
		ha.addDB(S1);
		ha.addDB(S2);
		
		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false, true);
		Assert.assertNull(selector.select());
	}
	
	@Test
	public void hasHASlavesOneMarkdownTest() throws DalException{
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		
		DalHA ha = new DalHA();
		ConfigBeanFactory.getMarkdownConfigBean().markdown(S2);
		
		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false, true);
		Assert.assertEquals(S1, selector.select());
	}
	
	@Test
	public void hasHASlavesOneMarkdownFailOverTest() throws DalException{
		List<DataBase> ss = new ArrayList<DataBase>();
		ss.add(new DataBase(S1, false, "", S1));
		ss.add(new DataBase(S2, false, "", S2));
		ss.add(new DataBase(S3, false, "", S3));
		
		DalHA ha = new DalHA();
		ha.addDB(S1);
		ConfigBeanFactory.getMarkdownConfigBean().markdown(S2);
		
		DatabaseSelector selector = new DatabaseSelector(ha, null, ss, false, true);
		Assert.assertEquals(S3, selector.select());
	}
}
