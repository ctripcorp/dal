package test.com.ctrip.platform.dal.dao.unittests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.Shard;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

public class TransactionTestClassSqlServer {
    public static final String DB_NAME = SqlServerDatabaseInitializer.DATABASE_NAME;
    public static final String DB_NAME_SHARD = "dao_test_sqlsvr_dbShard";
    
    @Transactional(logicalDbName = DB_NAME)
    public String perform() {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(DB_NAME, DalTransactionManager.getLogicDbName());
        return null;
    }

    @Transactional(logicalDbName = DB_NAME)
    public String performFail() {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(DB_NAME, DalTransactionManager.getLogicDbName());
        throw new RuntimeException();
    }

    @Transactional(logicalDbName = DB_NAME_SHARD)
    public String perform(@Shard String id) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(DB_NAME_SHARD, DalTransactionManager.getLogicDbName());
        assertEquals(id, DalTransactionManager.getCurrentDbMeta().getShardId());
        return null;
    }

    @Transactional(logicalDbName = DB_NAME_SHARD)
    public String perform(@Shard Integer id) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(DB_NAME_SHARD, DalTransactionManager.getLogicDbName());
        assertEquals(id.toString(), DalTransactionManager.getCurrentDbMeta().getShardId());
        return null;
    }

    @Transactional(logicalDbName = DB_NAME_SHARD)
    public String perform(@Shard int id) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(String.valueOf(id), DalTransactionManager.getCurrentDbMeta().getShardId());
        return null;
    }

    @Transactional(logicalDbName = DB_NAME_SHARD)
    public String perform(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(hints.getShardId(), DalTransactionManager.getCurrentDbMeta().getShardId());
        return null;
    }
    
    @Transactional(logicalDbName = DB_NAME_SHARD)
    public String performFail(String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(hints.getShardId(), DalTransactionManager.getCurrentDbMeta().getShardId());
        throw new RuntimeException();
    }
    
    @Transactional(logicalDbName = "dao_test_sqlsvr_dbShard")
    public String performWitShard(@Shard String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(id, DalTransactionManager.getCurrentDbMeta().getShardId());
        return null;
    }
    
    @Transactional(logicalDbName = "dao_test_sqlsvr_dbShard")
    public String performWitShardNest(@Shard String id, DalHints hints) {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(id, DalTransactionManager.getCurrentDbMeta().getShardId());
        performWitShard(id, hints);
        return null;
    }
    
    @Transactional(logicalDbName = "dao_test_sqlsvr_dbShard")
    public String performCommandWitShardNest(final @Shard String id, DalHints hints) throws SQLException {
        DalClientFactory.getClient(DB_NAME_SHARD).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                perform(id, new DalHints().inShard(id));
                perform(id, new DalHints().inShard(id));
                performWitShard(id, new DalHints().inShard(id));
                performWitShardNest(id, new DalHints().inShard(id));
                return false;
            }
        }, new DalHints().inShard(id));
        
        return "";
    }
    
    @Transactional(logicalDbName = "dao_test_sqlsvr_dbShard")
    public String performCommandWitShardNestFail(final @Shard String id, DalHints hints) throws SQLException {
        DalClientFactory.getClient(DB_NAME_SHARD).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                perform(id, new DalHints().inShard(id));
                perform(id, new DalHints().inShard(id));
                performWitShard(id, new DalHints().inShard(id));
                performWitShardNest(id, new DalHints().inShard(id));
                throw new RuntimeException();
            }
        }, new DalHints().inShard(id));
        
        return "";
    }
}
