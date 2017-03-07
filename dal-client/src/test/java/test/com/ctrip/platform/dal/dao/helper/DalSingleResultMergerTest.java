package test.com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DalSingleResultMerger;

public class DalSingleResultMergerTest {
	@Test
	public void testAddPartial(){
		DalSingleResultMerger<Object> test = new DalSingleResultMerger<>();
		try {
			test.addPartial("", null);
			test.addPartial("", new Object());
		} catch (SQLException e1) {
			Assert.fail();
		}
		try {
			test.addPartial("", new Object());
			Assert.fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testMerge() {
		DalSingleResultMerger<Object> test = new DalSingleResultMerger<>();
		
		try {
			test.addPartial("", null);
			test.addPartial("", "1");
		} catch (SQLException e1) {
			Assert.fail();
		}
		Assert.assertEquals("1", test.merge());
	}
	
	@Test
	public void testMergeNull() {
		DalSingleResultMerger<Object> test = new DalSingleResultMerger<>();
		
		try {
			test.addPartial("", null);
		} catch (SQLException e1) {
			Assert.fail();
		}
		Assert.assertNull(test.merge());
	}
}
