package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.junit.Test;

import com.ctrip.platform.dal.dao.StatementParameters;

public class StatementParametersTest {
	@Test
	public void testDuplicate() {
		StatementParameters test = new StatementParameters();
		test.set(1, "name1", Types.INTEGER, 1);
		test.set(2, "name2", Types.INTEGER, 2);
		StatementParameters test2 = test.duplicateWith("name1", 2);
		StatementParameters test3 = test.duplicateWith("name1", 3);
		assertEquals(2, test.size());
		assertEquals(2, test2.size());
		assertEquals(2, test3.size());
		
		assertEquals(1, test.get(0).getValue());
		assertEquals(2, test.get(1).getValue());

		assertEquals(2, test2.get(0).getValue());
		assertEquals(2, test2.get(1).getValue());

		assertEquals(3, test3.get(0).getValue());
		assertEquals(2, test3.get(1).getValue());
	}

	@Test
	public void testExpand() {
		StatementParameters test = new StatementParameters();
		List<Integer> values = new ArrayList<>();
		values.add(1);
		values.add(2);
		values.add(3);
		
		test.setInParameter(3, "name3", Types.INTEGER, values);
		test.set(2, "name2", Types.INTEGER, 2);
		test.set(1, "name1", Types.INTEGER, 1);
		
		test.compile();
		
		assertEquals("name1", test.get(0).getName());
		assertEquals("name2", test.get(1).getName());

		assertEquals("name3", test.get(2).getName());
		assertEquals("name3", test.get(3).getName());
		assertEquals("name3", test.get(4).getName());
		
		// check index
		assertEquals(1, test.get(0).getIndex());
		assertEquals(2, test.get(1).getIndex());

		assertEquals(3, test.get(2).getIndex());
		assertEquals(4, test.get(3).getIndex());
		assertEquals(5, test.get(4).getIndex());
		
		assertEquals(5, test.size());
	}
}
