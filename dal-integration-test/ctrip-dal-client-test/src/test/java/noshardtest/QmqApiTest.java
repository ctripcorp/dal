package noshardtest;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * To protect the api used by qmq-dal
 * @author Jason Song(song_s@ctrip.com)
 */
public class QmqApiTest {

  @Test
  public void testApi() throws Exception {
    DalConfigure dalConfigure = DalClientFactory.getDalConfigure();
    Set<String> names = dalConfigure.getDatabaseSetNames();

    final String someLogicDBName = "SimpleShardByDBOnMysql";
    final String someShardId = "0";

    assertTrue(names.contains(someLogicDBName));

    DatabaseSet dbSet = dalConfigure.getDatabaseSet(someLogicDBName);
    assertTrue(dbSet.isShardingSupported());

    DalClient dalClient = DalClientFactory.getClient(someLogicDBName);

    StatementParameters parameters = new StatementParameters();
    parameters.set(1, "");
    parameters.set(2, new Date());
    new DalHints().inDatabase("");

    dalClient.execute(new DalCommand() {
      @Override
      public boolean execute(DalClient client) throws SQLException {
        assertTrue(DalTransactionManager.isInTransaction());
        assertEquals(someLogicDBName, DalTransactionManager.getLogicDbName());
        assertTrue("DalService2DB_W".equalsIgnoreCase(DalTransactionManager.getCurrentDbMeta().getDataBaseKeyName()));
        assertEquals(someShardId, DalTransactionManager.getCurrentShardId());

        return true;
      }
    }, new DalHints().inShard(someShardId));
  }
}
