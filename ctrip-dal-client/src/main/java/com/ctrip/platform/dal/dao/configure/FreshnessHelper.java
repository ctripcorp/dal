package com.ctrip.platform.dal.dao.configure;

import java.sql.Types;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;
import com.ctrip.platform.dal.sql.logging.DalCLogger;
import com.ctrip.platform.dal.sql.logging.DalCatLogger;

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
    private static final String SECONDS_BEHIND_MASTER = "Seconds_Behind_Master";
    
    // read the select column MaxDelayTime
    private static final String SQLSVR_LATERNCY_SP = "{ ? = call Spb_ReplDelay}";
    private static final String MAX_DELAY_TIME = "MaxDelayTime";
    
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
            parameters.setResultsParameter(SECONDS_BEHIND_MASTER, new DalScalarExtractor());
            parameters.setResultsParameter("count");
            
            if(DalShardingHelper.isShardingEnabled(logicDbName))
                hints.inShard(getShardId(logicDbName, slaveDbName));
            
            DalClientFactory.getClient(logicDbName).call(MYSQL_LATERNCY_SP, parameters, hints.timeout(READ_TIMEOUT));
            freshness = parseFreshness(parameters, SECONDS_BEHIND_MASTER);
            log(slaveDbName, freshness);
        } catch (Throwable e) {
            DalClientFactory.getDalConfigure().getDalLogger().error(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, slaveDbName, e.getMessage()), e);
        }
        return freshness;
    }
    
    private static int getFromSqlserverSlave(String logicDbName, String slaveDbName) {
        DalHints hints = new DalHints().inDatabase(slaveDbName).slaveOnly().timeout(READ_TIMEOUT);
        int freshness = INVALID;
        try {
            StatementParameters parameters = new StatementParameters();
            parameters.registerOut(1, Types.INTEGER);
            parameters.setResultsParameter(MAX_DELAY_TIME, new DalScalarExtractor());
            
            if(DalShardingHelper.isShardingEnabled(logicDbName))
                hints.inShard(getShardId(logicDbName, slaveDbName));
            
            DalClientFactory.getClient(logicDbName).call(SQLSVR_LATERNCY_SP, parameters, hints);
            freshness = parseFreshness(parameters, MAX_DELAY_TIME);
            log(slaveDbName, freshness);
        } catch (Throwable e) {
            DalClientFactory.getDalConfigure().getDalLogger().error(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, slaveDbName, e.getMessage()), e);
        }
        return freshness;
    }
    
    private static int parseFreshness(StatementParameters parameters, String name) {
        Object ret = parameters.get(name, null).getValue();
        return ((Number)ret).intValue();
    }
    
    private static void log(String slaveDbName, Integer freshness) {
        DalCLogger.log(LogLevel.INFO, String.format("ReadWriteSplitting delay,titan key:%s,delay:%ds", slaveDbName, freshness));
        DalCatLogger.logEvent(String.format("DAL.ReadWriteSplittingDelay:%s", slaveDbName), freshness.toString());
    }
    
    private static String getShardId(String logicDbName, String slaveConnectionString) {
        for(DataBase slave: DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getDatabases().values()) {
            if(slave.getConnectionString().equalsIgnoreCase(slaveConnectionString))
                return slave.getSharding();
        }

        throw new IllegalStateException("Can not locate shard id for slave");
    }
}
