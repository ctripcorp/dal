package test.com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public class AbstractFreeSqlBuilderMeltdownWithSpaceSkipDisabledTest {
    private static final String logicDbName = "dao_test_sqlsvr_tableShard";
    private static final String tableName = "dal_client_test";

    @Test
    public void testEqual() throws SQLException {
        validate("equal", "[a] = ?");
        validate("equalNull", "");
        validate("equal AND equal", "[a] = ? AND [a] = ?");
        validate("equal AND equalNull", "[a] = ?");
        validate("equalNull AND equal", "[a] = ?");
        validate("equalNull AND equalNull", "");
        
        validate("( equal )", "( [a] = ? )");
        validate("( equalNull )", "");
        validate("( equal AND equal )", "( [a] = ? AND [a] = ? )");
        validate("( equal AND equalNull )", "( [a] = ? )");
        validate("( equalNull AND equal )", "( [a] = ? )");
        validate("( equalNull AND equalNull )", "");
    }
    
    @Test
    public void testLike() throws SQLException {
        validate("like", "[a] LIKE ?");
        validate("likeNull", "");
        validate("like AND like", "[a] LIKE ? AND [a] LIKE ?");
        validate("like AND likeNull", "[a] LIKE ?");
        validate("likeNull AND like", "[a] LIKE ?");
        validate("likeNull AND likeNull", "");
        
        validate("( like )", "( [a] LIKE ? )");
        validate("( likeNull )", "");
        validate("( like AND like )", "( [a] LIKE ? AND [a] LIKE ? )");
        validate("( like AND likeNull )", "( [a] LIKE ? )");
        validate("( likeNull AND like )", "( [a] LIKE ? )");
        validate("( likeNull AND likeNull )", "");
    }
    
    @Test
    public void testBetween() throws SQLException {
        validate("between", "[a] BETWEEN ? AND ?");
        validate("betweenNull", "");
        validate("between AND between", "[a] BETWEEN ? AND ? AND [a] BETWEEN ? AND ?");
        validate("between AND betweenNull", "[a] BETWEEN ? AND ?");
        validate("betweenNull AND between", "[a] BETWEEN ? AND ?");
        validate("betweenNull AND betweenNull", "");
        
        validate("( between )", "( [a] BETWEEN ? AND ? )");
        validate("( betweenNull )", "");
        validate("( between AND between )", "( [a] BETWEEN ? AND ? AND [a] BETWEEN ? AND ? )");
        validate("( between AND betweenNull )", "( [a] BETWEEN ? AND ? )");
        validate("( betweenNull AND between )", "( [a] BETWEEN ? AND ? )");
        validate("( betweenNull AND betweenNull )", "");
    }
    
    @Test
    public void testIsNull() throws SQLException {
        validate("isNull", "[a] IS NULL");
        validate("isNull AND isNull", "[a] IS NULL AND [a] IS NULL");
        
        validate("( isNull )", "( [a] IS NULL )");
        validate("( isNull AND isNull )", "( [a] IS NULL AND [a] IS NULL )");
    }
    
    @Test
    public void testIsNotNull() throws SQLException {
        validate("isNotNull", "[a] IS NOT NULL");
        validate("isNotNull AND isNotNull", "[a] IS NOT NULL AND [a] IS NOT NULL");
        
        validate("( isNotNull )", "( [a] IS NOT NULL )");
        validate("( isNotNull AND isNotNull )", "( [a] IS NOT NULL AND [a] IS NOT NULL )");
    }
    
    @Test
    public void testIn() throws SQLException {
        validate("in", "[a] IN ( ? )");
        validate("in AND in", "[a] IN ( ? ) AND [a] IN ( ? )");
        
        validate("inNull AND in", "[a] IN ( ? )");
        validate("in AND inNull", "[a] IN ( ? )");
        
        validate("in AND inNull OR in AND inNull", "[a] IN ( ? ) OR [a] IN ( ? )");
        
        validate("in AND inNull OR in AND inNull AND in OR inNull", "[a] IN ( ? ) OR [a] IN ( ? ) AND [a] IN ( ? )");
        
        validate("( in AND inNull ) OR ( in AND inNull AND in OR inNull OR in )", "( [a] IN ( ? ) ) OR ( [a] IN ( ? ) AND [a] IN ( ? ) OR [a] IN ( ? ) )");

        validate("( in ) OR ( in )", "( [a] IN ( ? ) ) OR ( [a] IN ( ? ) )");
        
        validate("( ( in ) OR in ) OR ( in OR ( in ) )", "( ( [a] IN ( ? ) ) OR [a] IN ( ? ) ) OR ( [a] IN ( ? ) OR ( [a] IN ( ? ) ) )");

        validate("( ( in ) OR in AND inNull ) OR ( in OR ( in ) )", "( ( [a] IN ( ? ) ) OR [a] IN ( ? ) ) OR ( [a] IN ( ? ) OR ( [a] IN ( ? ) ) )");

        validate("( ( ( in ) OR in AND inNull ) OR ( in OR ( in ) ) )", "( ( ( [a] IN ( ? ) ) OR [a] IN ( ? ) ) OR ( [a] IN ( ? ) OR ( [a] IN ( ? ) ) ) )");

        validate("( ( ( in ) OR in AND inNull ) OR ( ( in OR ( in ) ) ) )", "( ( ( [a] IN ( ? ) ) OR [a] IN ( ? ) ) OR ( ( [a] IN ( ? ) OR ( [a] IN ( ? ) ) ) ) )");
    }
    
    @Test
    public void testNotIn() throws SQLException {
        validate("notIn", "[a] NOT IN ( ? )");
        validate("notIn AND notIn", "[a] NOT IN ( ? ) AND [a] NOT IN ( ? )");
        
        validate("notInNull AND notIn", "[a] NOT IN ( ? )");
        validate("notIn AND notInNull", "[a] NOT IN ( ? )");
        
        validate("notIn AND notInNull OR notIn AND notInNull", "[a] NOT IN ( ? ) OR [a] NOT IN ( ? )");
        
        validate("notIn AND notInNull OR notIn AND notInNull AND notIn OR notInNull", "[a] NOT IN ( ? ) OR [a] NOT IN ( ? ) AND [a] NOT IN ( ? )");
        
        validate("( notIn AND notInNull ) OR ( notIn AND notInNull AND notIn OR notInNull OR notIn )", "( [a] NOT IN ( ? ) ) OR ( [a] NOT IN ( ? ) AND [a] NOT IN ( ? ) OR [a] NOT IN ( ? ) )");

        validate("( notIn ) OR ( notIn )", "( [a] NOT IN ( ? ) ) OR ( [a] NOT IN ( ? ) )");
        
        validate("( ( notIn ) OR notIn ) OR ( notIn OR ( notIn ) )", "( ( [a] NOT IN ( ? ) ) OR [a] NOT IN ( ? ) ) OR ( [a] NOT IN ( ? ) OR ( [a] NOT IN ( ? ) ) )");

        validate("( ( notIn ) OR notIn AND notInNull ) OR ( notIn OR ( notIn ) )", "( ( [a] NOT IN ( ? ) ) OR [a] NOT IN ( ? ) ) OR ( [a] NOT IN ( ? ) OR ( [a] NOT IN ( ? ) ) )");

        validate("( ( ( notIn ) OR notIn AND notInNull ) OR ( notIn OR ( notIn ) ) )", "( ( ( [a] NOT IN ( ? ) ) OR [a] NOT IN ( ? ) ) OR ( [a] NOT IN ( ? ) OR ( [a] NOT IN ( ? ) ) ) )");

        validate("( ( ( notIn ) OR notIn AND notInNull ) OR ( ( notIn OR ( notIn ) ) ) )", "( ( ( [a] NOT IN ( ? ) ) OR [a] NOT IN ( ? ) ) OR ( ( [a] NOT IN ( ? ) OR ( [a] NOT IN ( ? ) ) ) ) )");
    }
    
    @Test
    public void testNot() throws SQLException {
        validate("NOT equal", "NOT [a] = ?");
        validate("NOT equalNull", "");
        validate("NOT NOT NOT equal", "NOT NOT NOT [a] = ?");
        validate("NOT NOT NOT equalNull", "");
        validate("NOT equal AND NOT equal", "NOT [a] = ? AND NOT [a] = ?");
        validate("NOT equal AND NOT equalNull", "NOT [a] = ?");
        validate("NOT equalNull AND NOT equal", "NOT [a] = ?");
        validate("NOT equalNull AND NOT equalNull", "");
        
        validate("( NOT equal )", "( NOT [a] = ? )");
        validate("( NOT NOT NOT equal )", "( NOT NOT NOT [a] = ? )");
        validate("( NOT equalNull )", "");
        validate("( NOT NOT NOT equalNull )", "");
        validate("( NOT equal AND NOT equal )", "( NOT [a] = ? AND NOT [a] = ? )");
        validate("( NOT equal AND NOT equalNull )", "( NOT [a] = ? )");
        validate("( NOT equalNull AND NOT equal )", "( NOT [a] = ? )");
        validate("( NOT equalNull AND NOT equalNull )", "");
    }
    
    @Test
    public void testBracket() throws SQLException {
        validate("( ( equalNull ) )", "");
        validate("( ( ( equalNull ) ) )", "");
        validate("( ( ( equalNull ) ) ) AND ( ( ( equalNull ) ) )", "");
        validate("( ( ( equalNull ) ) ) OR ( ( ( equalNull ) ) )", "");
        validate("NOT ( NOT ( NOT ( NOT equalNull ) ) ) OR ( ( ( equalNull ) ) )", "");
    }
    
    @Test
    public void testOr() throws SQLException {
        validate("equal OR equal", "[a] = ? OR [a] = ?");
        validate("equal AND ( equal OR equal )", "[a] = ? AND ( [a] = ? OR [a] = ? )");
    }

    public void validate(String exp, String expected) throws SQLException {
        AbstractFreeSqlBuilder builder = new AbstractFreeSqlBuilder().setLogicDbName(logicDbName);
        builder.disableSpaceSkipping();
        
        // equal equalNull between betweenNull in inNull like likeNull isNull isNotNull AND OR NOT ( )
        String[] tokens = exp.split(" "); 
        for(String token: tokens) {
            switch (token) {
                case "equal":
                    builder.equal("a");
                    break;
                case "equalNull":
                    builder.equal("a").nullable(null);
                    break;
                case "like":
                    builder.like("a");
                    break;
                case "likeNull":
                    builder.like("a").nullable(null);
                    break;
                case "isNull":
                    builder.isNull("a");
                    break;
                case "isNotNull":
                    builder.isNotNull("a");
                    break;
                case "in":
                    builder.in("a");
                    break;
                case "notIn":
                    builder.notIn("a");
                    break;
                case "inNull":
                    builder.in("a").nullable(null);
                    break;
                case "notInNull":
                    builder.notIn("a").nullable(null);
                    break;
                case "between":
                    builder.between("a");
                    break;
                case "betweenNull":
                    builder.between("a").nullable(null);
                    break;
                case "AND":
                    builder.and();
                    break;
                case "OR":
                    builder.or();
                    break;
                case "NOT":
                    builder.not();
                    break;
                case "(":
                    builder.leftBracket();
                    break;
                case ")":
                    builder.rightBracket();
                    break;
                default:
                    Assert.fail("Unknown token: " + token);
            }
        }
        
        Assert.assertEquals(expected, builder.build());
    }
}
