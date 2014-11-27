package com.ctrip.platform.dal.dao.markdown;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class MarkupProcedureTest {

	static{
		try {
			ConfigBeanFactory.getMarkdownConfigBean().init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp() throws Exception{
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1000);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1000);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(false);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "1,3,5");
	}
	
	@Test
	public void onlyOnePhaseTest() throws Exception {
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3");
		
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
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3,5");
		
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
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3,5");
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
