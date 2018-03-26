package test.com.ctrip.platform.dal.dao.sqlbuilder;

import static junit.framework.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public class BaseQueryBuilderTest {
	
    public SelectSqlBuilder createTest(String tableName, DatabaseCategory dbCategory) throws SQLException {
        return new SelectSqlBuilder().from(tableName).setDatabaseCategory(dbCategory);
    }

	@Test
	public void testBuildList() throws SQLException {
		SelectSqlBuilder qb;

		qb = createTest("Test", DatabaseCategory.MySql);
		qb.select("columns").where("conditions").orderBy("ob", true);
		assertEquals("SELECT `columns` FROM `Test` WHERE conditions ORDER BY `ob` ASC", qb.build());
		assertEquals("SELECT `columns` FROM `Test_0` WHERE conditions ORDER BY `ob` ASC", qb.build("_0"));

		qb = createTest("Test", DatabaseCategory.SqlServer);
		qb.select("columns").where("conditions").orderBy("ob", true);
		assertEquals("SELECT [columns] FROM [Test] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build());
		assertEquals("SELECT [columns] FROM [Test_0] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build("_0"));
	}

	@Test
	public void testOrderBy() throws SQLException {
		SelectSqlBuilder qb;

		qb = createTest("Test", DatabaseCategory.MySql);
		qb.select("columns").where("conditions").orderBy("ob", true).orderBy("ob2", false).orderBy("ob3", true);
		assertEquals("SELECT `columns` FROM `Test` WHERE conditions ORDER BY `ob` ASC, `ob2` DESC, `ob3` ASC", qb.build());
		assertEquals("SELECT `columns` FROM `Test_0` WHERE conditions ORDER BY `ob` ASC, `ob2` DESC, `ob3` ASC", qb.build("_0"));

		qb = createTest("Test", DatabaseCategory.SqlServer);
		qb.select("columns").where("conditions").orderBy("ob", true).orderBy("ob2", false).orderBy("ob3", true);
		assertEquals("SELECT [columns] FROM [Test] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC, [ob2] DESC, [ob3] ASC", qb.build());
		assertEquals("SELECT [columns] FROM [Test_0] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC, [ob2] DESC, [ob3] ASC", qb.build("_0"));
	}

	@Test
	public void testBuildFirst() throws SQLException {
		SelectSqlBuilder qb;
		
		qb = createTest("Test", DatabaseCategory.MySql);
		qb.select("columns").where("conditions").orderBy("ob", true).requireFirst();
		assertEquals("SELECT `columns` FROM `Test` WHERE conditions ORDER BY `ob` ASC LIMIT 1", qb.build());
		assertEquals("SELECT `columns` FROM `Test_0` WHERE conditions ORDER BY `ob` ASC LIMIT 1", qb.build("_0"));

		qb = createTest("Test", DatabaseCategory.SqlServer);
		qb.select("columns").where("conditions").orderBy("ob", true).requireFirst();
		assertEquals("SELECT TOP 1 [columns] FROM [Test] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build());
		assertEquals("SELECT TOP 1 [columns] FROM [Test_0] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build("_0"));
	}

	@Test
	public void testBuildSingle() throws SQLException {
		SelectSqlBuilder qb;
		
		qb = createTest("Test", DatabaseCategory.MySql);
		qb.select("columns").where("conditions").orderBy("ob", true).requireSingle();
		assertEquals("SELECT `columns` FROM `Test` WHERE conditions ORDER BY `ob` ASC", qb.build());
		assertEquals("SELECT `columns` FROM `Test_0` WHERE conditions ORDER BY `ob` ASC", qb.build("_0"));

		qb = createTest("Test", DatabaseCategory.SqlServer);
		qb.select("columns").where("conditions").orderBy("ob", true).requireSingle();
		assertEquals("SELECT [columns] FROM [Test] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build());
		assertEquals("SELECT [columns] FROM [Test_0] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build("_0"));
	}

	@Test
	public void testBuildTop() throws SQLException {
		SelectSqlBuilder qb;
		
		qb = createTest("Test", DatabaseCategory.MySql);
		qb.select("columns").where("conditions").orderBy("ob", true).top(5);
		assertEquals("SELECT `columns` FROM `Test` WHERE conditions ORDER BY `ob` ASC LIMIT 5", qb.build());
		assertEquals("SELECT `columns` FROM `Test_0` WHERE conditions ORDER BY `ob` ASC LIMIT 5", qb.build("_0"));

		qb = createTest("Test", DatabaseCategory.SqlServer);
		qb.select("columns").where("conditions").orderBy("ob", true).top(5);
		assertEquals("SELECT TOP 5 [columns] FROM [Test] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build());
		assertEquals("SELECT TOP 5 [columns] FROM [Test_0] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC", qb.build("_0"));
	}
	
	@Test
	public void testBuildRange() throws SQLException {
		SelectSqlBuilder qb;
		
		qb = createTest("Test", DatabaseCategory.MySql);
		qb.select("columns").where("conditions").orderBy("ob", true).range(100,  200);
		assertEquals("SELECT `columns` FROM `Test` WHERE conditions ORDER BY `ob` ASC LIMIT 100, 200", qb.build());
		assertEquals("SELECT `columns` FROM `Test_0` WHERE conditions ORDER BY `ob` ASC LIMIT 100, 200", qb.build("_0"));

		qb = createTest("Test", DatabaseCategory.SqlServer);
		qb.select("columns").where("conditions").orderBy("ob", true).range(100,  200);
		assertEquals("SELECT [columns] FROM [Test] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC OFFSET 100 ROWS FETCH NEXT 200 ROWS ONLY", qb.build());
		assertEquals("SELECT [columns] FROM [Test_0] WITH (NOLOCK) WHERE conditions ORDER BY [ob] ASC OFFSET 100 ROWS FETCH NEXT 200 ROWS ONLY", qb.build("_0"));
	}
}
