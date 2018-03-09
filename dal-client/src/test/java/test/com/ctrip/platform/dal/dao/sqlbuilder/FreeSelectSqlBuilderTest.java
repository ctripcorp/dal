package test.com.ctrip.platform.dal.dao.sqlbuilder;

import static com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder.*;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.AND;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.equal;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.expression;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.like;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;

public class FreeSelectSqlBuilderTest {
    private static final String template = "template";
    private static final String wrappedTemplate = "[template]";
    private static final String expression = "count()";
    private static final String elseTemplate = "elseTemplate";
    private static final String EMPTY = "";
    private static final String logicDbName = "dao_test_sqlsvr_tableShard";
    
    private static final String tableName = "dal_client_test";
    private static final String wrappedTableName = "[dal_client_test]";
    
    private static final String noShardTableName = "noShard";
    private static final String wrappedNoShardTableName = "[noShard]";

    private FreeSelectSqlBuilder createTest() {
        return (FreeSelectSqlBuilder)new FreeSelectSqlBuilder().setLogicDbName(logicDbName).setHints(new DalHints());
    }
    
    @Test
    public void testCreate() throws SQLException {
        try {
            FreeSelectSqlBuilder test = new FreeSelectSqlBuilder(DatabaseCategory.MySql);
            test = new FreeSelectSqlBuilder(DatabaseCategory.SqlServer);
        } catch (Exception e) {
            fail();
        }
        try {
            FreeSelectSqlBuilder test = new FreeSelectSqlBuilder(DatabaseCategory.MySql);
            test.setLogicDbName(logicDbName);
            test.setDbCategory(DatabaseCategory.MySql);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testSetTemplate() throws SQLException {
        FreeSelectSqlBuilder test = createTest();
        test.setTemplate(template).setTemplate(template);
        assertEquals(template+" " + template, test.build());
    }
    
    @Test
    public void testBuildSqlServerTop() throws SQLException {
        FreeSelectSqlBuilder test = createTest();
        test.setTemplate(template).setTemplate(template).append(template);
        assertEquals("template template template", test.build());

        test.top(10);        
        assertEquals("template template template OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY", test.build());
    }
    
    @Test
    public void testBuildSqlServerAtPage() throws SQLException {
        FreeSelectSqlBuilder test = createTest();
        test.setTemplate(template).setTemplate(template).append(template);
        assertEquals("template template template", test.build());

        test.atPage(10, 10);
        assertEquals("template template template OFFSET 90 ROWS FETCH NEXT 10 ROWS ONLY", test.build());
    }
    
    @Test
    public void testBuildSqlServerRange() throws SQLException {
        FreeSelectSqlBuilder test = createTest();
        test.setTemplate(template).setTemplate(template).append(template);
        assertEquals("template template template", test.build());

        test.range(10, 10);
        assertEquals("template template template OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY", test.build());

    }

    @Test
    public void testBuildMySqlTop() throws SQLException {
        FreeSelectSqlBuilder test = new FreeSelectSqlBuilder(DatabaseCategory.MySql);
        test.setTemplate(template).setTemplate(template).append(template);
        assertEquals("template template template", test.build());

        test.top(10);        
        assertEquals("template template template limit 0, 10", test.build());
    }
    
    @Test
    public void testBuildMySqlAtPage() throws SQLException {
        FreeSelectSqlBuilder test = new FreeSelectSqlBuilder(DatabaseCategory.MySql);
        test.setTemplate(template).setTemplate(template).append(template);
        assertEquals("template template template", test.build());

        test.atPage(10, 10);
        assertEquals("template template template limit 90, 10", test.build());
    }
    
    @Test
    public void testBuildMySqlRange() throws SQLException {
        FreeSelectSqlBuilder test = new FreeSelectSqlBuilder(DatabaseCategory.MySql);
        test.setTemplate(template).setTemplate(template).append(template);
        assertEquals("template template template", test.build());

        test.range(10, 10);
        assertEquals("template template template limit 10, 10", test.build());

    }

    @Test
    public void testBuildSelect() throws SQLException {
        FreeSelectSqlBuilder test = createTest();
        test.select(template, template, template).from(tableName).where(text(template)).groupBy(template);
        test.top(10).setHints(new DalHints().inTableShard(1));
        assertEquals("SELECT [template], [template], [template] FROM [dal_client_test_1] WITH (NOLOCK) WHERE template GROUP BY [template] OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY", test.build());
    }

    @Test
    public void testBuildMeltdownAtEnd() throws SQLException {
        FreeSelectSqlBuilder test = createTest();
        test.where(text(template), AND, expression(template).nullable(null)).groupBy(template);
        assertEquals("WHERE template GROUP BY [template]", test.build());
        
        test = createTest();
        test.where(text(template)).and().appendExpression(template).nullable(null).groupBy(template);
        assertEquals("WHERE template GROUP BY [template]", test.build());
    }
    
    @Test
    public void testBuildMeltdownAtBegining() throws SQLException {
        FreeSelectSqlBuilder test = createTest();

        test = createTest();
        test.where(template).nullable(null).and().appendExpression(template).or().appendExpression(template).nullable(null).groupBy(template);
        assertEquals("WHERE template GROUP BY [template]", test.build());

    }
}
