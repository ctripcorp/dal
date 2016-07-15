package com.ctrip.platform.dal.dao.markdown;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class MarkupProcedureTest {
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
		
	@Before
	public void setUp() throws Exception{
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1000);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1000);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(false);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("1,3,5");
	}
	
	@Test
	public void onlyOnePhaseTest() throws Exception {
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3");
		
		MarkupProcedure procedure = new MarkupProcedure("dao_test");
		for (int i = 0; i < MarkupPhase.length; i++) {
			if(i >= 7)
				Assert.assertTrue(procedure.isPass());
			else
				Assert.assertFalse(procedure.isPass());
		}
		Assert.assertEquals(10, procedure.getCurrentPhase().getTotal());
		Assert.assertEquals(1, procedure.getNextPhaseIndex());
		Assert.assertEquals("total:10--loop:1", procedure.toString());
	}
	
	@Test
	public void twoPhasesTest() throws Exception{
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3,5");
		
		MarkupProcedure procedure = new MarkupProcedure("dao_test");
		for (int i = 0; i < MarkupPhase.length * 2; i++) {
			if(i >= 7 && i < 10 || i >= 15){
				Assert.assertTrue(procedure.isPass());
			}
			else
				Assert.assertFalse(procedure.isPass());		
		}
		Assert.assertEquals(10, procedure.getCurrentPhase().getTotal());
		Assert.assertEquals(2, procedure.getNextPhaseIndex());
	}
	
	@Test
	public void secondPhaseRollback() throws Exception{
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3,5");
		MarkupProcedure procedure = new MarkupProcedure("dao_test");
		for (int i = 0; i < MarkupPhase.length * 2; i++) {
			procedure.isPass();
			if(i == 13){
				Assert.assertEquals(2, procedure.getNextPhaseIndex());
			}
			if(i == 14){
				procedure.rollback();
			}			
			if(i == 15){
				Assert.assertEquals(1, procedure.getNextPhaseIndex());
			}
		}
		Assert.assertEquals(1, procedure.getNextPhaseIndex());
	}
}
