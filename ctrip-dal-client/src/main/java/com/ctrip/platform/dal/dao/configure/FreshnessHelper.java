package com.ctrip.platform.dal.dao.configure;

import java.sql.Types;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;

/**
 * It will use DalHintEnum.userDefined1
 * 
 * @author jhhe
 *
 */
public class FreshnessHelper {
    public static final int INVALID = -1;
    private static final int READ_TIMEOUT = 3;

    private static final String MYSQL_LATERNCY_SP = "{call sp_getslavestatus}";
    // read first column
    private static final String SQLSVR_LATERNCY_SP = "{ ? = call Spb_ReplDelay1}";
    
    public static int getSlaveFreshness(String logicDbName, String slaveDbName) {
        DatabaseCategory category = DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getDatabaseCategory();
        
        if(category == DatabaseCategory.MySql)
            return getFromMysqlSlave(logicDbName, slaveDbName);
        
        if(category == DatabaseCategory.SqlServer)
            return getFromSqlserverSlave(logicDbName, slaveDbName);
        
        return INVALID;
    }
    
    private static int getFromMysqlSlave(String logicDbName, String slaveDbName) {
        DalHints hints = new DalHints().inDatabase(slaveDbName).slaveOnly();
        int freshness = INVALID;
        try {
            StatementParameters parameters = new StatementParameters();
            parameters.setResultsParameter("Seconds_Behind_Master", new DalScalarExtractor());
            parameters.setResultsParameter("count");
            
            if(DalShardingHelper.isShardingEnabled(logicDbName))
                hints.inShard(getShardId(logicDbName, slaveDbName));
            
            Map<String, ?> result = DalClientFactory.getClient(logicDbName).call(MYSQL_LATERNCY_SP, parameters, hints.timeout(READ_TIMEOUT));
            Object ret = result.get("Seconds_Behind_Master");
            freshness = ((Number)ret).intValue();
        } catch (Throwable e) {
            DalClientFactory.getDalConfigure().getDalLogger().warn(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, slaveDbName, e.getMessage()));
        }
        return freshness;
    }
    
    private static int getFromSqlserverSlave(String logicDbName, String slaveDbName) {
        DalHints hints = new DalHints().inDatabase(slaveDbName).slaveOnly().timeout(READ_TIMEOUT);
        int freshness = INVALID;
        try {
            StatementParameters parameters = new StatementParameters();
            parameters.registerOut(1, Types.INTEGER);
            
            if(DalShardingHelper.isShardingEnabled(logicDbName))
                hints.inShard(getShardId(logicDbName, slaveDbName));
            
            DalClientFactory.getClient(logicDbName).call(SQLSVR_LATERNCY_SP, parameters, hints);
            freshness = parameters.get(0).getValue();
        } catch (Throwable e) {
            DalClientFactory.getDalConfigure().getDalLogger().warn(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, slaveDbName, e.getMessage()));
        }
        return freshness;
    }
    
    private static String getShardId(String logicDbName, String slaveConnectionString) {
        for(DataBase slave: DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getDatabases().values()) {
            if(slave.getConnectionString().equalsIgnoreCase(slaveConnectionString))
                return slave.getSharding();
        }

        throw new IllegalStateException("Can not locate shard id for slave");
    }
}
