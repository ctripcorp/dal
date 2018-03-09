package test.com.ctrip.platform.dal.dao.sqlbuilder;

import static org.junit.Assert.assertEquals;
import static com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder.*;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;

public class FreeUpdateSqlBuilderTest {
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

    private FreeUpdateSqlBuilder createTest() {
        return (FreeUpdateSqlBuilder)new FreeUpdateSqlBuilder(DatabaseCategory.MySql).setLogicDbName(logicDbName).setHints(new DalHints());
    }
    
    @Test
    public void testSetTemplate() throws SQLException {
        FreeUpdateSqlBuilder test = createTest();
        test.setTemplate(template);
        assertEquals(template, test.build());
    }
    
    @Test
    public void testInsertInto() throws SQLException {
        FreeUpdateSqlBuilder test = createTest();

        test.insertInto(noShardTableName);
        assertEquals("INSERT INTO " + wrappedNoShardTableName, test.build());
        
        test = createTest();

        test.insertInto(table(noShardTableName));
        assertEquals("INSERT INTO " + wrappedNoShardTableName, test.build());
    }
    
    @Test
    public void testValues() throws SQLException {
        FreeUpdateSqlBuilder test = createTest();

        test.values(noShardTableName);
        assertEquals("([noShard]) VALUES (?)", test.build());
        
        test = createTest();
        test.values(noShardTableName, tableName);
        assertEquals("([noShard], [dal_client_test]) VALUES (?, ?)", test.build());
        
        test = createTest();
        test.insertInto(noShardTableName);
        test.values(noShardTableName, tableName);
        assertEquals("INSERT INTO [noShard] ([noShard], [dal_client_test]) VALUES (?, ?)", test.build());
    }
    
    @Test
    public void testDeleteFrom() throws SQLException {
        FreeUpdateSqlBuilder test = createTest();

        test.deleteFrom(noShardTableName);
        assertEquals("DELETE FROM " + wrappedNoShardTableName, test.build());
        
        test = createTest();

        test.deleteFrom(table(noShardTableName));
        assertEquals("DELETE FROM " + wrappedNoShardTableName, test.build());
        
        test = createTest();

        test.deleteFrom(table(noShardTableName));
        test.where(template);
        assertEquals("DELETE FROM " + wrappedNoShardTableName + " WHERE " + template, test.build());
    }
    
    @Test
    public void testDeleteFromMeltdown() throws SQLException {
        FreeUpdateSqlBuilder test = createTest();

        test.deleteFrom(noShardTableName);
        test.where().and(template ,template, template);
        assertEquals("DELETE FROM " + wrappedNoShardTableName + " WHERE template AND template AND template", test.build());
    }
    
    @Test
    public void testUpdate() throws SQLException {
        FreeUpdateSqlBuilder test = createTest();

        test.update(noShardTableName);
        assertEquals("UPDATE " + wrappedNoShardTableName, test.build());
        
        test = createTest();

        test.update(table(noShardTableName));
        assertEquals("UPDATE " + wrappedNoShardTableName, test.build());
        
        test = createTest();
    }
    
    @Test
    public void testSet() throws SQLException {
        FreeUpdateSqlBuilder test = createTest();

        test.set(template, tableName, noShardTableName);
        assertEquals("SET [template]=?, [dal_client_test]=?, [noShard]=?", test.build());
        
        test = createTest();

        test.update(table(noShardTableName));
        test.set(template, tableName, noShardTableName);
        
        assertEquals("UPDATE " + wrappedNoShardTableName + " SET [template]=?, [dal_client_test]=?, [noShard]=?", test.build());
        
        test = createTest();
    }
}
