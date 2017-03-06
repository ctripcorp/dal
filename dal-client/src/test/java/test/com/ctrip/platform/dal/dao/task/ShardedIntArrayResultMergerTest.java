package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.task.ShardedIntArrayResultMerger;

public class ShardedIntArrayResultMergerTest {

	@Test
	public void testMerge() {
		ShardedIntArrayResultMerger test = new ShardedIntArrayResultMerger();
		try {
			test.recordPartial("1", new Integer[] {0, 1, 2, 3});
			test.addPartial("1", new int[] {0, 1, 2, 3});
			
			test.recordPartial("2", new Integer[] {4, 5, 6});
			test.addPartial("2", new int[] {4, 5, 6});

			test.recordPartial("3", new Integer[] {7, 8, 9});
			test.addPartial("3", new int[] {7, 8, 9});
			
			int[] result = test.merge();
			Assert.assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, result);

		} catch (SQLException e) {
			fail();
		}
	}

}
