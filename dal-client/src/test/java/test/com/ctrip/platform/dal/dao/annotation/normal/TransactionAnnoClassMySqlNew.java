package test.com.ctrip.platform.dal.dao.annotation.normal;

import java.sql.SQLException;

import org.springframework.stereotype.Component;

import test.com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.ctrip.platform.dal.dao.annotation.Shard;

@Component("TransactionAnnoClassMySqlNew")
public class TransactionAnnoClassMySqlNew extends BaseTransactionAnnoClass {
    public static final String DB_NAME = MySqlDatabaseInitializer.DATABASE_NAME;
    public static final String DB_NAME_SHARD = "dao_test_mysql_dbShard";
    
    public TransactionAnnoClassMySqlNew() {
        super(DB_NAME, DB_NAME_SHARD, "select 1");
    }
    
    @DalTransactional(logicDbName = DB_NAME)
    public String perform() {
        return super.perform();
    }

    @DalTransactional(logicDbName = DB_NAME)
    public String performFail() {
        return super.performFail();
    }

    @DalTransactional(logicDbName = DB_NAME)
    public String performNest() {
        return super.performNest();
    }

    public String performNest2() {
        return super.performNest2();
    }

    public String performNest3() throws InstantiationException, IllegalAccessException {
        return super.performNest3();
    }

    @DalTransactional(logicDbName = DB_NAME)
    public String performNestDistributedTransaction() {
        return super.performNestDistributedTransaction();
    }

    @DalTransactional(logicDbName = DB_NAME)
    public String performDistributedTransaction() {
        return super.performDistributedTransaction();
    }

    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String perform(@Shard String id) {
        return super.perform(id);
    }

    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String perform(@Shard Integer id) {
        return super.perform(id);
    }

    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String perform(@Shard int id) {
        return super.perform(id);
    }

    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String perform(String id, DalHints hints) {
        return super.perform(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performFail(String id, DalHints hints) {
        return super.performFail(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performWitShard(@Shard String id, DalHints hints) {
        return super.performWitShard(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performWitShardNest(@Shard String id, DalHints hints) {
        return super.performWitShardNest(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performWitShardNestConflict(@Shard String id, DalHints hints) {
        return super.performWitShardNestConflict(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performWitShardNestFail(@Shard String id, DalHints hints) {
        return super.performWitShardNestFail(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performCommandWitShardNest(final @Shard String id, DalHints hints) throws SQLException {
        return super.performCommandWitShardNest(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performCommandWitShardNestFail(final @Shard String id, DalHints hints) throws SQLException {
        return super.performCommandWitShardNestFail(id, hints);
    }
    
    @DalTransactional(logicDbName = DB_NAME_SHARD)
    public String performDetectDistributedTransaction(final @Shard String id, DalHints hints) throws SQLException {
        return super.performDetectDistributedTransaction(id, hints);
    }
}
