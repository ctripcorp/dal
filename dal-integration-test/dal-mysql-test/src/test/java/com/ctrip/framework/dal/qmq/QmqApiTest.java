package com.ctrip.framework.dal.qmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;

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
        assertEquals("DalService2DB_W", DalTransactionManager.getCurrentDbMeta().getDataBaseKeyName());
        assertEquals(someShardId, DalTransactionManager.getCurrentShardId());

        return true;
      }
    }, new DalHints().inShard(someShardId));
  }
}
