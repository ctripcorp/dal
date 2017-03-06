package test.com.ctrip.platform.dal.dao.sqlbuilder;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Types;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.InsertSqlBuilder;

public class InsertSqlBuilderTest {
	@Test
	public void testBuildMySql() throws SQLException {
		InsertSqlBuilder isb = new InsertSqlBuilder().from("table").setDatabaseCategory(DatabaseCategory.MySql);
		
		isb.set("f1", 1, Types.INTEGER);
		isb.setSensitive("f2", "abc", Types.VARBINARY);
		
		assertEquals("INSERT INTO `table` (`f1`, `f2`) VALUES(?, ?)", isb.build());
		assertEquals("INSERT INTO `table_0` (`f1`, `f2`) VALUES(?, ?)", isb.build("_0"));
		
		StatementParameters p = isb.buildParameters();
		assertEquals(2, p.size());
	}

	@Test
	public void testBuildSqlsvr() throws SQLException {
		InsertSqlBuilder isb = new InsertSqlBuilder().from("table").setDatabaseCategory(DatabaseCategory.SqlServer);
		
		isb.set("f1", 1, Types.INTEGER);
		isb.setSensitive("f2", "abc", Types.VARBINARY);
		
		assertEquals("INSERT INTO [table] ([f1], [f2]) VALUES(?, ?)", isb.build());
		assertEquals("INSERT INTO [table_0] ([f1], [f2]) VALUES(?, ?)", isb.build("_0"));
		
		StatementParameters p = isb.buildParameters();
		assertEquals(2, p.size());
	}
}
