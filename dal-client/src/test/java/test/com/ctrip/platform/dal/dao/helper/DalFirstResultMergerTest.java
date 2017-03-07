package test.com.ctrip.platform.dal.dao.helper;

import java.util.Comparator;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DalFirstResultMerger;

public class DalFirstResultMergerTest {
	@Test
	public void testAddPartial() {
		DalFirstResultMerger<Object> test = new DalFirstResultMerger<>();
		test.addPartial("", null);
		test.addPartial("", new Object());
	}

	@Test
	public void testMergeWithComparator() {
		DalFirstResultMerger<Integer> test = new DalFirstResultMerger<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
		
		test.addPartial("", 1);
		test.addPartial("", 2);
		Assert.assertEquals(2, test.merge().intValue());
	}
	
	@Test
	public void testMergeWithoutComparator() {
		DalFirstResultMerger<Integer> test = new DalFirstResultMerger<>();
		
		test.addPartial("", null);
		test.addPartial("", null);
		Assert.assertNull(test.merge());
	}
}
