package test.com.ctrip.platform.dal.dao.sqlbuilder;

import static com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder.*;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder.Text;
import com.ctrip.platform.dal.dao.sqlbuilder.Clause;
import com.ctrip.platform.dal.dao.sqlbuilder.Expressions.Expression;
import com.ctrip.platform.dal.dao.sqlbuilder.MatchPattern;

public class AbstractFreeSqlBuilderTest {
    private static final String template = "template";
    private static final String wrappedTemplate = "[template]";
    private static final String expression = "count()";
    private static final String elseTemplate = "elseTemplate";
    private static final String EMPTY = "";
    private static final String logicDbName = "dao_test_sqlsvr_tableShard";
    private static final String tableName = "dal_client_test";
    
    @Test
    public void testSetLogicDbName() {
        AbstractFreeSqlBuilder test = create();
        try {
            test.setLogicDbName(null);
            fail();
        } catch (Exception e) {
        }
        
        try {
            test.setLogicDbName("Not exist");
            fail();
        } catch (IllegalArgumentException e) {
        } catch(Throwable ex) {
            fail();
        }
        
        test.setLogicDbName(logicDbName);
    }
    
    /**
     * Create test with auto meltdown disabled
     * @return
     */
    private AbstractFreeSqlBuilder create() {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        StatementParameters p = new StatementParameters();
        test.with(p);
        return test;
    }
    
    @Test
    public void testSetHints() {
        AbstractFreeSqlBuilder test = create();
        try {
            test.setHints(null);
            fail();
        } catch (Exception e) {
        }
        
        test.setHints(new DalHints());
    }
    
    @Test
    public void testWith() {
        AbstractFreeSqlBuilder test = create();
        try {
            test.with(null);
            fail();
        } catch (Exception e) {
        }
        
        StatementParameters p = new StatementParameters();
        test.with(p);
        // Same is allowed
        test.with(p);

        //Empty is allowed
        p = new StatementParameters();
        test.with(p);
        p.set("", 1, "");
        
        p = new StatementParameters();
        try {
            test.with(p);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testText() {
        AbstractFreeSqlBuilder test = create();
        test.append(text(template));
        assertEquals(template, test.build());
    }    
    
    @Test
    public void testTexts() {
        AbstractFreeSqlBuilder test = create();
        test.append(texts(template, template));
        assertEquals(template+ " " + template, test.build());
    }    
    
    @Test
    public void testKeyword() {
        AbstractFreeSqlBuilder test = create();
        test.append(keyword(template));
        assertEquals(template, test.build());
    }    
    
    @Test
    public void testColumn() {
        AbstractFreeSqlBuilder test = create();
        test.append(column(template));
        assertEquals(wrappedTemplate, test.build());
    }    
    
    @Test
    public void testColumns() {
        AbstractFreeSqlBuilder test = create();
        test.append(columns(template, template, template));
        assertEquals("[template] [template] [template]", test.build());
    }    
        
    @Test
    public void testTable() {
        AbstractFreeSqlBuilder test = create();
        test.append(table(template));
        assertEquals(wrappedTemplate, test.build());
        
        test = create();
        test.append(table(tableName));
        test.setHints(new DalHints().inTableShard(1));
        assertEquals("[dal_client_test_1]", test.build());
    }
    
    @Test
    public void testSet() {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        StatementParameters p = new StatementParameters();
        test.with(p);
        test.select(template).from(template).where(equal(template)).set(template, "abc", Types.VARCHAR);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE [template] = ?", test.build());
        StatementParameters parameters = test.buildParameters();
        assertEquals(1, parameters.size());
        assertEquals(template, parameters.get(0).getName());
    }
    
    @Test
    public void testSetNullable() {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        StatementParameters p = new StatementParameters();
        test.with(p);
        test.select(template).from(template).where(equal(template)).setNullable("abc", null, Types.VARCHAR).nullable(null);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE", test.build());
        StatementParameters parameters = test.buildParameters();
        assertEquals(0, parameters.size());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        p = new StatementParameters();
        test.with(p);
        test.select(template).from(template).where(equal(template)).setNullable("abc", null, Types.VARCHAR);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE [template] = ?", test.build());
        parameters = test.buildParameters();
        assertEquals(0, parameters.size());
    }
    
    @Test
    public void testSetWhen() {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        StatementParameters p = new StatementParameters();
        test.with(p);
        test.select(template).from(template).where(equal(template)).set(false, template, "abc", Types.VARCHAR).when(false);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE", test.build());
        StatementParameters parameters = test.buildParameters();
        assertEquals(0, parameters.size());
    }
    
    @Test
    public void testSetIn() throws SQLException {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        StatementParameters p = new StatementParameters();
        test.with(p);
        
        List<String> paras = new ArrayList<>();
        paras.add("abc1");
        paras.add("abc2");
        paras.add("abc3");
        
        test.select(template).setIn(template, paras, Types.VARCHAR);
        assertEquals("SELECT [template]", test.build());
        StatementParameters parameters = test.buildParameters();
        assertEquals(1, parameters.size());
        assertTrue(parameters.get(0).isInParam());
    }

    @Test
    public void testSetInWhen() throws SQLException {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        StatementParameters p = new StatementParameters();
        test.with(p);
        
        List<String> paras = new ArrayList<>();
        paras.add("abc1");
        paras.add("abc2");
        paras.add("abc3");
        
        test.select(template).setIn(false, template, paras, Types.VARCHAR);
        assertEquals("SELECT [template]", test.build());
        StatementParameters parameters = test.buildParameters();
        assertEquals(0, parameters.size());
    }

    @Test
    public void testSetInNullable() throws SQLException {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        StatementParameters p = new StatementParameters();
        test.with(p);
        
        List<String> paras = new ArrayList<>();
        paras.add("abc1");
        paras.add("abc2");
        paras.add("abc3");
        
        test.select(template).setInNullable(template, paras, Types.VARCHAR);
        assertEquals("SELECT [template]", test.build());
        StatementParameters parameters = test.buildParameters();
        assertEquals(1, parameters.size());
        assertTrue(parameters.get(0).isInParam());
        
        //Null case
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        p = new StatementParameters();
        test.with(p);
        
        paras = new ArrayList<>();
        paras.add("abc1");
        paras.add("abc2");
        paras.add("abc3");
        
        test.select(template).setInNullable(template, null, Types.VARCHAR);
        assertEquals("SELECT [template]", test.build());
        parameters = test.buildParameters();
        assertEquals(0, parameters.size());
        
        //Empty case
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        p = new StatementParameters();
        test.with(p);
        
        paras = new ArrayList<>();
        
        test.select(template).setInNullable(template, null, Types.VARCHAR);
        assertEquals("SELECT [template]", test.build());
        parameters = test.buildParameters();
        assertEquals(0, parameters.size());
    }
    
    @Test
    public void testAppend() {
        AbstractFreeSqlBuilder test = create();
        test.append(template);
        assertEquals(template, test.build());
        
        test = create();
        test.append(template, template, template);
        assertEquals("template template template", test.build());
    }

    @Test
    public void testAppendCondition() {
        AbstractFreeSqlBuilder test = create();
        test.appendWhen(true, template);
        assertEquals(template, test.build());
        
        test = create();
        test.appendWhen(false, template);
        assertEquals(EMPTY, test.build());
    }
    
    @Test
    public void testAppendConditionWithElse() {
        AbstractFreeSqlBuilder test = create();
        test.appendWhen(true, template, elseTemplate);
        assertEquals(template, test.build());
        
        test = create();
        test.appendWhen(false, template, elseTemplate);
        assertEquals(elseTemplate, test.build());
    }
    
    @Test
    public void testAppendClause() {
        AbstractFreeSqlBuilder test = create();
        test.append(new Text(template));
        assertEquals(template, test.build());
    }

    @Test
    public void testAppendClauseCondition() {
        AbstractFreeSqlBuilder test = create();
        test.appendWhen(true, new Text(template));
        assertEquals(template, test.build());
        
        test = create();
        test.appendWhen(false, new Text(template));
        assertEquals(EMPTY, test.build());
    }
    
    @Test
    public void testAppendClauseConditionWithElse() {
        AbstractFreeSqlBuilder test = create();
        test.appendWhen(true, new Text(template), new Text(elseTemplate));
        assertEquals(template, test.build());
        
        test = create();
        test.appendWhen(false, new Text(template), new Text(elseTemplate));
        assertEquals(elseTemplate, test.build());
    }
    
    @Test
    public void testAppendWith() {
        AbstractFreeSqlBuilder test = create();
        test.appendWith(COMMA, new Text(template), new Text(elseTemplate));
        assertEquals("template, elseTemplate", test.build());
        
    }
    
    @Test
    public void testAppendColumn() {
        AbstractFreeSqlBuilder test = create();
        test.appendColumn(template);
        test.setLogicDbName(logicDbName);
        assertEquals("[" + template + "]", test.build());
        
        test = create();
        test.appendColumn(template, template);
        test.setLogicDbName(logicDbName);
        assertEquals("[" + template + "] AS " + template, test.build());

        test = create();
        test.append(column(template).as(template));
        test.setLogicDbName(logicDbName);
        assertEquals("[" + template + "] AS " + template, test.build());
    }
    
    @Test
    public void testAppendTable() {
        String noShardTable = "noShard";
        
        AbstractFreeSqlBuilder test = create();
        test.appendTable(noShardTable);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("[" + noShardTable + "]", test.build());
        
        test = create();
        test.appendTable(tableName);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints().inTableShard(1));
        assertEquals("[" + tableName + "_1]", test.build());
        
        test = create();
        test.appendTable(tableName, template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints().inTableShard(1));
        assertEquals("[" + tableName + "_1] AS " + template, test.build());

        test = create();
        test.append(table(tableName).as(template));
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints().inTableShard(1));
        assertEquals("[" + tableName + "_1] AS " + template, test.build());
    }
    
    @Test
    public void testSelect() {
        String noShardTable = "noShard";
        
        AbstractFreeSqlBuilder test = create();
        test.select();
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("SELECT", test.build());
        
        test = create();
        test.select(template, template, template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("SELECT [template], [template], [template]", test.build());
        
        test = create();
        test.select(text(template), expression(template), column(template).as(template));
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("SELECT template, template, [template] AS template", test.build());
    }
    
    @Test
    public void testSelectAll() {
        AbstractFreeSqlBuilder test = create();
        test.selectAll();
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("SELECT *", test.build());
    }
    
    @Test
    public void testFrom() {
        String noShardTable = "noShard";
        
        AbstractFreeSqlBuilder test = create();
        test.from(noShardTable);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("FROM [noShard] WITH (NOLOCK)", test.build());
        
        test = create();
        test.from(table(noShardTable));
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("FROM [noShard] WITH (NOLOCK)", test.build());
        
        test = create();
        test.from(table(noShardTable));
        test.setLogicDbName("dao_test");
        test.setHints(new DalHints());
        assertEquals("FROM `noShard`", test.build());
    }
    
    @Test
    public void testWhere() {
        AbstractFreeSqlBuilder test = create();
        test.where();
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("WHERE", test.build());
        
        test = create();
        test.where(template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("WHERE template", test.build());
        
        test = create();
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        test.where("count()", template);
        assertEquals("WHERE count() template", test.build());
    }
    
    @Test
    public void testWhereClause() {
        AbstractFreeSqlBuilder test = create();
        test.where(expression("count()"), text(template));
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("WHERE count() template", test.build());
    }
    
    @Test
    public void testIncludeAll() {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.select(template).from(template).where(AbstractFreeSqlBuilder.includeAll()).equal(template);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE 1=1 AND [template] = ?", test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.select(template).from(template).where(AbstractFreeSqlBuilder.includeAll()).equal(template).nullable(null);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE 1=1", test.build());
    }
    
    @Test
    public void testExcludeAll() {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.select(template).from(template).where(AbstractFreeSqlBuilder.excludeAll()).equal(template);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE 1<>1 OR [template] = ?", test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.select(template).from(template).where(AbstractFreeSqlBuilder.excludeAll()).equal(template).nullable(null);
        assertEquals("SELECT [template] FROM [template] WITH (NOLOCK) WHERE 1<>1", test.build());
    }    
    
    @Test
    public void testOrderBy() {
        AbstractFreeSqlBuilder test = create();
        test.orderBy(template, true);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("ORDER BY " + wrappedTemplate + " ASC", test.build());
    }
    
    @Test
    public void testGroupBy() {
        AbstractFreeSqlBuilder test = create();
        test.groupBy(template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("GROUP BY " + wrappedTemplate, test.build());
        
        test = create();
        test.groupBy(expression(template));
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("GROUP BY " + template, test.build());
    }
    
    @Test
    public void testHaving() {
        AbstractFreeSqlBuilder test = create();
        test.having(template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("HAVING " + template, test.build());
    }
    
    @Test
    public void testLeftBracket() {
        AbstractFreeSqlBuilder test = create();
        test.leftBracket();
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("(", test.build());
    }
    
    @Test
    public void testRightBracket() {
        AbstractFreeSqlBuilder test = create();
        test.rightBracket();
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals(")", test.build());
    }
    
    @Test
    public void testBracket() {
        //Empty
        AbstractFreeSqlBuilder test = create();
        test.bracket(template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("(template)", test.build());
        
        test = create();
        test.bracket(template, template, template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("(template template template)", test.build());
        
        //One
        test = create();
        test.bracket(text(template));
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("(template)", test.build());
        
        //two
        test = create();
        test.bracket(text(template), expression(expression));
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals("(template " + expression + ")", test.build());
    }
    
    @Test
    public void testAnd() {
        AbstractFreeSqlBuilder test = create();
        test.and();
        assertEquals("", test.build());
        
        test = create();
        test.and(template);
        assertEquals(template, test.build());

        test = create();
        test.and(template, template);
        assertEquals("template AND template", test.build());
        
        test = create();
        test.and(expressions(template));
        assertEquals("template", test.build());
        
        test = create();
        test.and(expressions(template, template));
        assertEquals("template AND template", test.build());

        test = create();
        test.and(expressions(template, template, template));
        assertEquals("template AND template AND template", test.build());
    }
    
    @Test
    public void testOr() {
        AbstractFreeSqlBuilder test = create();
        test.or();
        assertEquals("", test.build());

        test = create();
        test.or(template);
        assertEquals(template, test.build());
        
        test = create();
        test.or(template, template);
        assertEquals("template OR template", test.build());
        
        test = create();
        test.or(expressions(template));
        assertEquals(template, test.build());
        
        test = create();
        test.or(expressions(template, expression));
        assertEquals(template + " OR " + expression, test.build());
        
        test = create();
        test.or(expressions(template, template, template));
        assertEquals("template OR template OR template", test.build());
    }
    
    @Test
    public void testNot() {
        AbstractFreeSqlBuilder test = create();
        test.not();
        assertEquals("NOT", test.build());
    }
    
    @Test
    public void testNullable() {
        AbstractFreeSqlBuilder test = create();
        Expression exp;
        try {
            test.nullable(null);
            fail();
        } catch (Exception e) {
        }
        
        test = create();
        try {
            test.append(template).nullable(null);
            fail();
        } catch (Exception e) {
        }

        test = create();
        try {
            test.append(template).nullable(new Object());
            fail();
        } catch (Exception e) {
        }

        test = create();
        exp = new Expression(expression);
        test.append(template).append(exp).nullable(null);
        assertTrue(exp.isInvalid());
        assertEquals(template, test.build());

        test = create();
        exp = new Expression(expression);
        test.append(template).append(exp).nullable(new Object());
        assertTrue(exp.isValid());
        assertEquals(template + " " + expression, test.build());        
    }
    
    @Test
    public void testWhen() {
        AbstractFreeSqlBuilder test = create();
        Expression exp;
        try {
            test.when(false);
            fail();
        } catch (Exception e) {
        }
        
        test = create();
        try {
            test.append(template).when(false);
            fail();
        } catch (Exception e) {
        }

        test = create();
        try {
            test.append(template).when(true);
            fail();
        } catch (Exception e) {
        }

        test = create();
        exp = new Expression(expression);
        test.append(template).append(exp).when(false);
        assertTrue(exp.isInvalid());
        assertEquals(template, test.build());
        
        test = create();
        exp = new Expression(expression);
        test.append(template).append(exp).when(true);
        assertTrue(exp.isValid());
        assertEquals(template + " " + expression, test.build());
    }
    
    private interface ExpressionProvider {
        AbstractFreeSqlBuilder createExp();
        AbstractFreeSqlBuilder createExpWithParameter();
        AbstractFreeSqlBuilder createExpWithNullParameter();
    }
    
    public void testExpression(String result, ExpressionProvider provider) {
        testExpression(result, 1, provider);
    }
    
    public void testExpression(String result, int count, ExpressionProvider provider) {
        testExpr(result, 0, provider.createExp());
        testExpr(result, count, provider.createExpWithParameter());
        
        testExpr(result, 0, provider.createExp().nullable(new Object()));        
        testExpr(result, count, provider.createExpWithParameter().nullable(new Object()));        

        testExpr(result, 0, provider.createExp().when(true));
        testExpr(result, count, provider.createExpWithParameter().when(true));
        
        testNullError(provider.createExp());
        testNull(provider.createExpWithNullParameter().nullable());
        testExpr(result, count, provider.createExpWithParameter().nullable());
        
        
        testNull(provider.createExp().nullable(null));
        testNull(provider.createExpWithParameter().nullable(null));
        
        testNull(provider.createExp().when(false));
        testNull(provider.createExpWithParameter().when(false));
    }
    
    @Test
    public void testEqual() {
        testExpression(wrappedTemplate + " = ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().equal(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().equal(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().equal(template, null, Types.VARCHAR);
            }
        });
    }
    
    private void testExpr(String result, int count, AbstractFreeSqlBuilder builder) {
        assertEquals(result, builder.build());
        assertEquals(count, builder.buildParameters().size());
    }
    
    private void testNull(AbstractFreeSqlBuilder builder) {
        testExpr("", 0, builder);
    }
    
    private void testNullError(AbstractFreeSqlBuilder builder) {
        try {
            builder.nullable();
            fail();
        } catch (Throwable e) {
        }
    }
    
    @Test
    public void testNotEqual() {
        testExpression(wrappedTemplate + " <> ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notEqual(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().notEqual(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notEqual(template, null, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testGreaterThan() {
        testExpression(wrappedTemplate + " > ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().greaterThan(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().greaterThan(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().greaterThan(template, null, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testGreaterThanEquals() {
        testExpression(wrappedTemplate + " >= ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().greaterThanEquals(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().greaterThanEquals(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().greaterThanEquals(template, null, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testLessThan() {
        testExpression(wrappedTemplate + " < ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().lessThan(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().lessThan(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().lessThan(template, null, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testLessThanEquals() {
        testExpression(wrappedTemplate + " <= ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().lessThanEquals(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().lessThanEquals(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().lessThanEquals(template, null, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testLike() {
        testExpression(wrappedTemplate + " LIKE ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().like(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().like(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().like(template, null, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testLikePattern() {
        testExpression(wrappedTemplate + " LIKE ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().like(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().like(template, "abc", MatchPattern.END_WITH, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().like(template, null, MatchPattern.END_WITH, Types.VARCHAR);
            }
        });
        
        AbstractFreeSqlBuilder builder = create().like(template, "abc", MatchPattern.END_WITH, Types.VARCHAR);
        assertEquals("%abc", builder.buildParameters().get(0).getValue());
        
        builder = create().like(template, "abc", MatchPattern.BEGIN_WITH, Types.VARCHAR);
        assertEquals("abc%", builder.buildParameters().get(0).getValue());
        
        builder = create().like(template, "abc", MatchPattern.CONTAINS, Types.VARCHAR);
        assertEquals("%abc%", builder.buildParameters().get(0).getValue());
        
        builder = create().like(template, "a%b%c", MatchPattern.USER_DEFINED, Types.VARCHAR);
        assertEquals("a%b%c", builder.buildParameters().get(0).getValue());
    }
    
    @Test
    public void testNotLike() {
        testExpression(wrappedTemplate + " NOT LIKE ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notLike(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().notLike(template, "abc", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notLike(template, null, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testNotLikePattern() {
        testExpression(wrappedTemplate + " NOT LIKE ?", new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notLike(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().notLike(template, "abc", MatchPattern.BEGIN_WITH, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notLike(template, null, MatchPattern.BEGIN_WITH, Types.VARCHAR);
            }
        });
        
        AbstractFreeSqlBuilder builder = create().notLike(template, "abc", MatchPattern.END_WITH, Types.VARCHAR);
        assertEquals("%abc", builder.buildParameters().get(0).getValue());
        
        builder = create().notLike(template, "abc", MatchPattern.BEGIN_WITH, Types.VARCHAR);
        assertEquals("abc%", builder.buildParameters().get(0).getValue());
        
        builder = create().notLike(template, "abc", MatchPattern.CONTAINS, Types.VARCHAR);
        assertEquals("%abc%", builder.buildParameters().get(0).getValue());
        
        builder = create().notLike(template, "a%b%c", MatchPattern.USER_DEFINED, Types.VARCHAR);
        assertEquals("a%b%c", builder.buildParameters().get(0).getValue());
    }
    
    @Test
    public void testBetween() {
        String result = wrappedTemplate + " BETWEEN ? AND ?";
        testExpression(result, 2, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().between(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().between(template, "abc", "def", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().between(template, null, null, Types.VARCHAR);
            }
        });
        
        testExpression(result, 2, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().between(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().between(template, "abc", "def", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().between(template, "abc", null, Types.VARCHAR);
            }
        });

        testExpression(result, 2, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().between(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().between(template, "abc", "def", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().between(template, null, "abc", Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testNotBetween() {
        String result = wrappedTemplate + " NOT BETWEEN ? AND ?";
        testExpression(result, 2, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notBetween(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().notBetween(template, "abc", "def", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notBetween(template, null, null, Types.VARCHAR);
            }
        });
        
        testExpression(result, 2, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notBetween(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().notBetween(template, "abc", "def", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notBetween(template, "abc", null, Types.VARCHAR);
            }
        });

        testExpression(result, 2, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notBetween(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                return create().notBetween(template, "abc", "def", Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notBetween(template, null, "abc", Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testIn() {
        String result = wrappedTemplate + " IN ( ? )";
        testExpression(result, 1, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().in(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                List l = new ArrayList<>();
                l.add("abc");
                return create().in(template, l, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().in(template, null, Types.VARCHAR);
            }
        });

        testExpression(result, 1, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().in(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                List l = new ArrayList<>();
                l.add("abc");
                return create().in(template, l, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().in(template, new ArrayList<>(), Types.VARCHAR);
            }
        });
        
        testExpression(result, 1, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().in(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                List l = new ArrayList<>();
                l.add("abc");
                return create().in(template, l, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                List l = new ArrayList<>();
                l.add(null);
                return create().in(template, l, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testNotIn() {
        String result = wrappedTemplate + " NOT IN ( ? )";
        testExpression(result, 1, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notIn(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                List l = new ArrayList<>();
                l.add("abc");
                return create().notIn(template, l, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notIn(template, null, Types.VARCHAR);
            }
        });

        testExpression(result, 1, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notIn(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                List l = new ArrayList<>();
                l.add("abc");
                return create().notIn(template, l, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                return create().notIn(template, new ArrayList<>(), Types.VARCHAR);
            }
        });
        
        testExpression(result, 1, new ExpressionProvider() {
            public AbstractFreeSqlBuilder createExp() {
                return create().notIn(template);
            }
            public AbstractFreeSqlBuilder createExpWithParameter() {
                List l = new ArrayList<>();
                l.add("abc");
                return create().notIn(template, l, Types.VARCHAR);
            }
            public AbstractFreeSqlBuilder createExpWithNullParameter() {
                List l = new ArrayList<>();
                l.add(null);
                return create().notIn(template, l, Types.VARCHAR);
            }
        });
    }
    
    @Test
    public void testIsNull() {
        AbstractFreeSqlBuilder test = create();
        test.isNull(template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals(wrappedTemplate + " IS NULL", test.build());
        assertEquals(0, test.buildParameters().size());
    }
    
    @Test
    public void testIsNotNull() {
        AbstractFreeSqlBuilder test = create();
        test.isNotNull(template);
        test.setLogicDbName(logicDbName);
        test.setHints(new DalHints());
        assertEquals(wrappedTemplate + " IS NOT NULL", test.build());
        assertEquals(0, test.buildParameters().size());
    }
    
    @Test
    public void testExpression() throws SQLException {
        Clause test = expression(template);
        
        AbstractFreeSqlBuilder builder = create();
        builder.append(test);
        builder.setLogicDbName(logicDbName);

        assertEquals(template, test.build());
    }
    
    @Test
    public void testAutoMeltdown() throws SQLException {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.append(AND).bracket(AND, OR, AND);
        assertEquals("", test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.append(expression(template), AND).bracket(AND, OR, AND);
        assertEquals(template, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND).appendColumn(template);
        assertEquals(template + " " +wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND).appendTable(template);
        assertEquals(template + " " + wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template)).nullable(null).append(AND).bracket(AND, OR, AND).appendTable(template);
        assertEquals(wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND).appendTable(template).append(AND).append(expression(template)).nullable(null);
        assertEquals(template+ " " + wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND, expression(template)).appendTable(template).append(AND).append(expression(template)).nullable(null);
        assertEquals("template AND (template) [template]", test.build());
    }

    @Test
    public void testAutoMeltdownWhen() throws SQLException {
        AbstractFreeSqlBuilder test = new AbstractFreeSqlBuilder();
        test.append(AND).bracket(AND, OR, AND);
        assertEquals("", test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.append(expression(template), AND).bracket(AND, OR, AND);
        assertEquals(template, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND).appendColumn(template);
        assertEquals(template + " " +wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND).appendTable(template);
        assertEquals(template + " " + wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template)).when(false).append(AND).bracket(AND, OR, AND).appendTable(template);
        assertEquals(wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND).appendTable(template).append(AND).append(expression(template)).when(Boolean.FALSE == null);
        assertEquals(template+ " " + wrappedTemplate, test.build());
        
        test = new AbstractFreeSqlBuilder();
        test.setLogicDbName(logicDbName);
        test.append(expression(template), AND).bracket(AND, OR, AND, expression(template)).appendTable(template).append(AND).append(expression(template)).when(Boolean.FALSE == null);
        assertEquals("template AND (template) [template]", test.build());
    }
}
