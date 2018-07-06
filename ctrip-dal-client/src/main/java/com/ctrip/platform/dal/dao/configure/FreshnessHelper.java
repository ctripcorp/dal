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
public class FreshnessHelper implements FreshnessReader {
    public static final int INVALID = -1;
    private static final int READ_TIMEOUT = 3;

    private static final String FRESHNESS = "Freshness";
    // This is actually useless, but we need to define it in order to match the execution of SP
    private static final String COUNT = "Count";
    
    private static final String MYSQL_LATERNCY_SP = "{call sp_getslavestatus}";
    
    // read the select column MaxDelayTime
    private static final String SQLSVR_LATERNCY_SP = "{ ? = call Spb_ReplDelay}";
    
    public int getSlaveFreshness(String logicDbName, String slaveConnectionString) {
        DatabaseCategory category = DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getDatabaseCategory();
        
        if(category == DatabaseCategory.MySql)
            return getFromMysqlSlave(logicDbName, slaveConnectionString);
        
        if(category == DatabaseCategory.SqlServer)
            return getFromSqlserverSlave(logicDbName, slaveConnectionString);
        
        return INVALID;
    }
    
    
    private int getFromMysqlSlave(String logicDbName, String slaveConnectionString) {
        int freshness = INVALID;
        try {
            DalHints hints = new DalHints().inDatabase(slaveConnectionString).slaveOnly().timeout(READ_TIMEOUT);
            if(DalShardingHelper.isShardingEnabled(logicDbName))
                hints.inShard(getShardId(logicDbName, slaveConnectionString));
            
            StatementParameters parameters = new StatementParameters();
            parameters.setResultsParameter("Freshness", new DalScalarExtractor());
            parameters.setResultsParameter(COUNT);
                        
            DalClientFactory.getClient(logicDbName).call(MYSQL_LATERNCY_SP, parameters, hints.timeout(READ_TIMEOUT));
            freshness = parseFreshness(slaveConnectionString, parameters.getResultParameterByName("Freshness").getValue());
        } catch (Throwable e) {
            DalClientFactory.getDalConfigure().getDalLogger().error(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, slaveConnectionString, e.getMessage()), e);
        }
        return freshness;
    }
    
    private int getFromSqlserverSlave(String logicDbName, String slaveConnectionString) {
        int freshness = INVALID;
        try {
            DalHints hints = new DalHints().inDatabase(slaveConnectionString).slaveOnly().timeout(READ_TIMEOUT);
            if(DalShardingHelper.isShardingEnabled(logicDbName))
                hints.inShard(getShardId(logicDbName, slaveConnectionString));

            StatementParameters parameters = new StatementParameters();
            parameters.registerOut(1, Types.INTEGER);
            parameters.setResultsParameter(FRESHNESS, new DalScalarExtractor());
            
            DalClientFactory.getClient(logicDbName).call(SQLSVR_LATERNCY_SP, parameters, hints);
            freshness = parseFreshness(slaveConnectionString, parameters.getResultParameterByName(FRESHNESS).getValue());
        } catch (Throwable e) {
            DalClientFactory.getDalConfigure().getDalLogger().error(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, slaveConnectionString, e.getMessage()), e);
        }
        return freshness;
    }
    
    private int parseFreshness(String slaveConnectionString, Object ret) {
        Integer freshness = ((Number)ret).intValue();

        DalCLogger.log(LogLevel.INFO, String.format("ReadWriteSplitting delay,titan key:%s,delay:%ds", slaveConnectionString, freshness));
        DalCatLogger.logEvent(String.format("DAL.ReadWriteSplittingDelay:%s", slaveConnectionString), freshness.toString());

        return freshness;
    }
    
    private String getShardId(String logicDbName, String slaveConnectionString) {
        for(DataBase slave: DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getDatabases().values()) {
            if(slave.getConnectionString().equalsIgnoreCase(slaveConnectionString))
                return slave.getSharding();
        }

        throw new IllegalStateException("Can not locate shard id for slave");
    }
}
