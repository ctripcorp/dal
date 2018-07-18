package test.com.ctrip.platform.dal.dao.sqlbuilder;

import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.AND;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.NOT;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.OR;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.between;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.bracket;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.equal;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.expression;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.greaterThan;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.greaterThanEquals;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.in;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.leftBracket;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.lessThan;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.lessThanEquals;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.like;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.notEqual;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.notIn;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.notLike;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.rightBracket;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.Clause;

public class ExpressionsTest {
    private static final String template = "template";
    private static final String wrappedTemplate = "[template]";
    private static final String expression = "count()";
    private static final String elseTemplate = "elseTemplate";
    private static final String EMPTY = "";
    private static final String logicDbName = "dao_test_sqlsvr_tableShard";
    private static final String tableName = "dal_client_test";
    private static final String templateWIthHolder = "AAA %s BBB";

    @Test
    public void testConditionExpression() throws SQLException {
        Clause test = expression(true, template);
        
        setEnv(test);
        assertEquals(template, test.build());
        
        test = expression(false, template);
        
        setEnv(test);
        assertEquals("", test.build());
        
        test = expression(true, template, wrappedTemplate);
        setEnv(test);

        assertEquals(template, test.build());
        
        test = expression(false, template, wrappedTemplate);
        setEnv(test);

        assertEquals(wrappedTemplate, test.build());
    }
    
    @Test
    public void testLeftBracket() throws SQLException {
        Clause test = leftBracket;
        setEnv(test);

        assertEquals("(", test.build());
    }
    
    @Test
    public void testRightBracket() throws SQLException {
        Clause test = rightBracket;
        setEnv(test);

        assertEquals(")", test.build());
    }
    
    @Test
    public void testBracket() throws SQLException {
        Clause test = bracket(expression(template));
        setEnv(test);

        assertEquals("("+template+")", test.build());
    }
    
    @Test
    public void testAnd() throws SQLException {
        Clause test = AND;
        setEnv(test);

        assertEquals("AND", test.build());
    }
    
    @Test
    public void testOr() throws SQLException {
        Clause test = OR;
        setEnv(test);

        assertEquals("OR", test.build());
    }
    
    @Test
    public void testNot() throws SQLException {
        Clause test = NOT;
        setEnv(test);

        assertEquals("NOT", test.build());
    }
    
    @Test
    public void testEqual() throws SQLException {
        Clause test = equal(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " = ?", test.build());
    }
    
    @Test
    public void testNotEqual() throws SQLException {
        Clause test = notEqual(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " <> ?", test.build());
    }
    
    @Test
    public void testGreaterThan() throws SQLException {
        Clause test = greaterThan(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " > ?", test.build());
    }
    
    @Test
    public void testGreaterThanEquals() throws SQLException {
        Clause test = greaterThanEquals(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " >= ?", test.build());
    }
    
    @Test
    public void testLessThan() throws SQLException {
        Clause test = lessThan(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " < ?", test.build());
    }
    
    @Test
    public void testLessThanEquals() throws SQLException {
        Clause test = lessThanEquals(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " <= ?", test.build());
    }
    
    @Test
    public void testBetween() throws SQLException {
        Clause test = between(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " BETWEEN ? AND ?", test.build());
    }
    
    @Test
    public void testLike() throws SQLException {
        Clause test = like(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " LIKE ?", test.build());
    }
    
    @Test
    public void testNotlike() throws SQLException {
        Clause test = notLike(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " NOT LIKE ?", test.build());
    }
    
    @Test
    public void testIn() throws SQLException {
        Clause test = in(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " IN ( ? )", test.build());
    }
    
    @Test
    public void testNotIn() throws SQLException {
        Clause test = notIn(template);
        setEnv(test);

        assertEquals(wrappedTemplate + " NOT IN ( ? )", test.build());
    }
    
    private void setEnv(Clause test) {
        AbstractFreeSqlBuilder builder = new AbstractFreeSqlBuilder();
        builder.append(test);
        builder.setLogicDbName(logicDbName);
    }
}
