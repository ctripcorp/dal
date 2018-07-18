package test.com.ctrip.platform.dal.dao.annotation.normal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

public class BaseTransactionAnnoClass {
    private String noShardDb;
    private String shardDb;
    private String query;

    public String getNoShardDb() {
        return noShardDb;
    }

    public String getShardDb() {
        return shardDb;
    }

    public static final String DONE = "done";

    public BaseTransactionAnnoClass(String noShardDb, String shardDb, String query) {
        this.noShardDb = noShardDb;
        this.shardDb = shardDb;
        this.query = query;
    }
    
    @Autowired
    private JustAnotherClass jac;
    
    public JustAnotherClass getJac() {
        return jac;
    }
    
    public String performNormal() {
        assertTrue(!DalTransactionManager.isInTransaction());
        return DONE;
    }

    public String perform() {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(noShardDb, DalTransactionManager.getLogicDbName());
        testQuery(noShardDb);
        return DONE;
    }

    public String performFail() {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(noShardDb, DalTransactionManager.getLogicDbName());
        testQuery(noShardDb);
        throw new RuntimeException();
    }

    public String performNest() {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(noShardDb, DalTransactionManager.getLogicDbName());
        perform();
        return DONE;
    }

    public String performNest2() {
        assertTrue(!DalTransactionManager.isInTransaction());
        perform();
        return DONE;
    }

    public String performNest3() throws InstantiationException, IllegalAccessException {
        assertTrue(!DalTransactionManager.isInTransaction());
        TransactionAnnoClassSqlServer target = DalTransactionManager.create(TransactionAnnoClassSqlServer.class);
        target.perform();
        return DONE;
    }

    public String performNestDistributedTransaction() {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(noShardDb, DalTransactionManager.getLogicDbName());
        perform(1);
        fail();
        return DONE;
    }

    public String performDistributedTransaction() {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(noShardDb, DalTransactionManager.getLogicDbName());
        testQueryFail(shardDb);
        return DONE;
    }

    public String perform(String id) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(shardDb, DalTransactionManager.getLogicDbName());
        assertEquals(id, DalTransactionManager.getCurrentShardId());
        return DONE;
    }

    public String perform(Integer id) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(shardDb, DalTransactionManager.getLogicDbName());
        assertEquals(id.toString(), DalTransactionManager.getCurrentShardId());
        return DONE;
    }

    public String perform(int id) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(String.valueOf(id), DalTransactionManager.getCurrentShardId());
        testQuery(shardDb);
        return DONE;
    }

    public String perform(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(hints.getShardId(), DalTransactionManager.getCurrentShardId());
        testQuery(shardDb);
        return DONE;
    }
    
    public String performFail(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(hints.getShardId(), DalTransactionManager.getCurrentShardId());
        testQuery(shardDb);
        throw new RuntimeException();
    }
    
    public String performWitShard(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        if(id != null)
            assertEquals(id, DalTransactionManager.getCurrentShardId());
        else
            assertEquals(hints.getShardId(), DalTransactionManager.getCurrentShardId());
        testQuery(shardDb);
        return DONE;
    }
    
    public String performWitShardNest(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        if(id != null)
            assertEquals(id, DalTransactionManager.getCurrentShardId());
        else
            assertEquals(hints.getShardId(), DalTransactionManager.getCurrentShardId());
        performWitShard(id, hints);
        return DONE;
    }
    
    public String performWitShardNestConflict(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(id, DalTransactionManager.getCurrentShardId());
        performWitShard(id+id, hints);
        fail();
        return DONE;
    }
    
    public String performWitShardNestFail(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        if(id != null)
            assertEquals(id, DalTransactionManager.getCurrentShardId());
        else
            assertEquals(hints.getShardId(), DalTransactionManager.getCurrentShardId());
        performFail(id, hints.inShard(id));
        return DONE;
    }
    
    public String performCommandWitShardNest(final String id, DalHints hints) throws SQLException {
        DalClientFactory.getClient(shardDb).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                perform(id, new DalHints().inShard(id));
                perform(id, new DalHints().inShard(id));
                performWitShard(id, new DalHints());
                performWitShardNest(id, new DalHints().inShard(id+id));
                return false;
            }
        }, new DalHints().inShard(id));
        testQuery(shardDb);

        return DONE;
    }
    
    public String performCommandWitShardNestFail(final String id, DalHints hints) throws SQLException {
        DalClientFactory.getClient(shardDb).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                perform(id, new DalHints().inShard(id));
                perform(id, new DalHints().inShard(id));
                performWitShard(id, new DalHints().inShard(id));
                performWitShardNest(id, new DalHints().inShard(id));
                performFail(id, new DalHints().inShard(id));
                fail();
                return false;
            }
        }, new DalHints().inShard(id));
        
        return DONE;
    }
    
    public String performDetectDistributedTransaction(final String id, DalHints hints) throws SQLException {
        DalClientFactory.getClient(shardDb).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                perform(id, new DalHints().inShard(id));
                perform(id, new DalHints().inShard(id));
                performWitShard(id, new DalHints().inShard(id));
                performWitShardNest(id, new DalHints().inShard(id));
                performWitShardNest(id+id, new DalHints());
                fail();
                return false;
            }
        }, new DalHints().inShard(id));
        
        return DONE;
    }
    
    private void testQuery(String db) {
        try {
            new DalQueryDao(db).query(query, new StatementParameters(), new DalHints(), Integer.class);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    private void testQueryFail(String db) {
        try {
            new DalQueryDao(db).query(query, new StatementParameters(), new DalHints(), Integer.class);
            fail();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
