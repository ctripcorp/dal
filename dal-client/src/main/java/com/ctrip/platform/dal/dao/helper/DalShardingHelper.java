package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.*;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.dao.strategy.ClusterShardStrategyAdapter;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;

public class DalShardingHelper {
    public static ILogger logger = DalElementFactory.DEFAULT.getILogger();

    private static final String CROSSSHARD_BULKREQUEST_IN_TRANSACTION = "CrossShardBulkRequestInTransaction";
    private static final String CROSSSHARD_BULKREQUEST = "CrossShardBulkRequest";

    public static boolean isShardingEnabled(String logicDbName) {
        return getDatabaseSet(logicDbName).isShardingSupported();
    }

    public static boolean isTableShardingEnabled(String logicDbName, String tableName) {
        return getDatabaseSet(logicDbName).isTableShardingSupported(tableName);
    }

    // reserved
    public static String buildShardStr(String logicDbName, String shardId) throws SQLException {
        String separator = getDatabaseSet(logicDbName).getStrategy().getTableShardSeparator();
        return separator == null ? shardId : separator + shardId;
    }

    public static String buildShardStr(String logicDbName, String rawTableName, String shardId) throws SQLException {
        String separator;
        DalShardingStrategy strategy = getDatabaseSet(logicDbName).getStrategy();
        if (strategy instanceof ClusterShardStrategyAdapter)
            separator = ((ClusterShardStrategyAdapter) strategy).getTableShardSeparator(rawTableName);
        else
            separator = strategy.getTableShardSeparator();
        return separator == null ? shardId : separator + shardId;
    }

    public static DatabaseSet getDatabaseSet(String logicDbName) {
        return DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName);
    }

    /**
     * Try to locate DB shard id by hints. If can not be located, return false.
     *
     * @param logicDbName
     * @param hints
     * @return true if shard id can be located
     * @throws SQLException
     */
    public static boolean locateShardId(String logicDbName, DalHints hints) throws SQLException {
        DalConfigure config = DalClientFactory.getDalConfigure();

        DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
        String shardId = dbSet.getStrategy().locateDbShard(config, logicDbName, hints);
        if (shardId == null)
            return false;

        // Fail fast asap
        dbSet.validate(shardId);
        hints.inShard(shardId);

        return true;
    }

    /**
     * Locate table shard id by hints.
     *
     * @param logicDbName
     * @param hints
     * @return
     * @throws SQLException
     */
    public static boolean locateTableShardId(String logicDbName, String tableName, DalHints hints)
            throws SQLException {
        DalConfigure config = DalClientFactory.getDalConfigure();
        DalShardingStrategy strategy = config.getDatabaseSet(logicDbName).getStrategy();

        // First check if we can locate the table shard id with the original hints
        String tableShardId = strategy.locateTableShard(config, logicDbName, tableName, hints);
        if (tableShardId == null)
            return false;

        hints.inTableShard(tableShardId);
        return true;
    }

    /**
     * Locate table shard id by hints.
     *
     * @param logicDbName
     * @param hints
     * @return
     * @throws SQLException
     * @deprecated use below locateTableShardId method with tableName parameter
     */
    public static String locateTableShardId(String logicDbName, DalHints hints, StatementParameters parameters,
            Map<String, ?> fields) throws SQLException {
        return locateTableShardId(logicDbName, null, hints, parameters, fields);
    }

    /**
     * Locate table shard id by hints.
     *
     * @param logicDbName
     * @param hints
     * @return
     * @throws SQLException
     */
    public static String locateTableShardId(String logicDbName, String tableName, DalHints hints,
            StatementParameters parameters, Map<String, ?> fields) throws SQLException {
        DalConfigure config = DalClientFactory.getDalConfigure();
        DalShardingStrategy strategy = config.getDatabaseSet(logicDbName).getStrategy();

        // First check if we can locate the table shard id with the original hints
        String shard = strategy.locateTableShard(config, logicDbName, tableName, hints);
        if (shard != null) {
            hints.inTableShard(shard);
            return shard;
        }

        shard = strategy.locateTableShard(config, logicDbName, tableName,
                new DalHints().setParameters(parameters).setFields(fields));
        if (shard != null) {
            hints.inTableShard(shard);
            return shard;
        }

        throw new SQLException("Can not locate table shard for " + logicDbName);

    }

    /**
     * Group pojos by shard id. Should be only used for DB that support sharding.
     *
     * @param logicDbName
     * @param shardId
     * @param daoPojos
     * @return Grouped pojos
     * @throws SQLException In case locate shard id faild
     */
    public static Map<String, Map<Integer, Map<String, ?>>> shuffle(String logicDbName, String shardId,
            List<Map<String, ?>> daoPojos) throws SQLException {
        return shuffle(logicDbName, shardId, daoPojos, null);
    }

    public static Map<String, Map<Integer, Map<String, ?>>> shuffle(String logicDbName, String shardId,
                                                                    List<Map<String, ?>> daoPojos, DalHints hints) throws SQLException {
        Map<String, Map<Integer, Map<String, ?>>> shuffled = getPojosGroupedByShardId(logicDbName, shardId, daoPojos, hints);
        validateShardIdsExistInDBSet(logicDbName, shuffled.keySet());
        return shuffled;
    }

    public static Map<String, Map<Integer, Map<String, ?>>> getPojosGroupedByShardId(String logicDbName, String shardId,
                                                                                     List<Map<String, ?>> daoPojos) throws SQLException {
        return getPojosGroupedByShardId(logicDbName, shardId, daoPojos, null);
    }

    public static Map<String, Map<Integer, Map<String, ?>>> getPojosGroupedByShardId(String logicDbName, String shardId,
                                                                                     List<Map<String, ?>> daoPojos, DalHints hints) throws SQLException {
        Map<String, Map<Integer, Map<String, ?>>> shuffled = new HashMap<>();
        DalConfigure config = DalClientFactory.getDalConfigure();
        DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
        DalShardingStrategy strategy = dbSet.getStrategy();
        DalHints tmpHints = new DalHints();
        if (hints != null) {
            tmpHints = hints.clone();
            tmpHints.setRequestContext(hints.getRequestContext());
        }
        for (int i = 0; i < daoPojos.size(); i++) {
            Map<String, ?> pojo = daoPojos.get(i);

            String tmpShardId =
                    shardId == null ? strategy.locateDbShard(config, logicDbName, tmpHints.setFields(pojo)) : shardId;

            Map<Integer, Map<String, ?>> pojosInShard = shuffled.get(tmpShardId);
            if (pojosInShard == null) {
                pojosInShard = new LinkedHashMap<>();
                shuffled.put(tmpShardId, pojosInShard);
            }

            pojosInShard.put(i, pojo);
        }
        return shuffled;
    }

    public static void validateShardIdsExistInDBSet(String logicDbName, Set<String> shardIds) throws SQLException {
        if (shardIds == null || shardIds.isEmpty())
            return;

        DalConfigure config = DalClientFactory.getDalConfigure();
        DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
        for (String shardId : shardIds) {
            dbSet.validate(shardId);
        }
    }

    /**
     * Shuffle by given values like id list for DB shard
     *
     * @param logicDbName
     * @param parameters
     * @return
     * @throws SQLException
     */
    public static Map<String, List<?>> shuffle(String logicDbName, List<?> parameters) throws SQLException {
        return shuffle(logicDbName, parameters, null);
    }

    public static Map<String, List<?>> shuffle(String logicDbName, List<?> parameters, DalHints hints) throws SQLException {
        Map<String, List<?>> shuffled = getParamsGroupedByShardId(logicDbName, parameters, hints);
        validateShardIdsExistInDBSet(logicDbName, shuffled.keySet());
        return shuffled;
    }

    public static Map<String, List<?>> getParamsGroupedByShardId(String logicDbName, List<?> parameters)
            throws SQLException {
        return getParamsGroupedByShardId(logicDbName, parameters, null);
    }

    public static Map<String, List<?>> getParamsGroupedByShardId(String logicDbName, List<?> parameters, DalHints hints)
            throws SQLException {
        Map<String, List<?>> shuffled = new HashMap<>();

        DalConfigure config = DalClientFactory.getDalConfigure();

        DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
        DalShardingStrategy strategy = dbSet.getStrategy();

        DalHints tmpHints = new DalHints();
        if (hints != null){
            tmpHints = hints.clone();
            tmpHints.setRequestContext(hints.getRequestContext());
        }
        for (int i = 0; i < parameters.size(); i++) {
            Object value = parameters.get(i);

            String tmpShardId = strategy.locateDbShard(config, logicDbName, tmpHints.setShardValue(value));
            // If this can not be located
            if (tmpShardId == null)
                throw new NullPointerException("Can not locate shard id for " + value);

            List pojosInShard = shuffled.get(tmpShardId);
            if (pojosInShard == null) {
                pojosInShard = new LinkedList();
                shuffled.put(tmpShardId, pojosInShard);
            }

            pojosInShard.add(value);
        }
        return shuffled;
    }

    /**
     * Shuffle by table shard id.
     *
     * @param logicDbName
     * @param pojos
     * @return
     * @throws SQLException
     * @deprecated use below shuffleByTable method with tableName parameter
     */
    public static Map<String, Map<Integer, Map<String, ?>>> shuffleByTable(String logicDbName, String tableShardId,
            Map<Integer, Map<String, ?>> pojos) throws SQLException {
        return shuffleByTable(logicDbName, null, tableShardId, pojos);
    }

    /**
     * Shuffle by table shard id.
     *
     * @param logicDbName
     * @param pojos
     * @return
     * @throws SQLException
     */
    public static Map<String, Map<Integer, Map<String, ?>>> shuffleByTable(String logicDbName, String tableName,
            String tableShardId, Map<Integer, Map<String, ?>> pojos) throws SQLException {
        Map<String, Map<Integer, Map<String, ?>>> shuffled = new HashMap<>();
        DalConfigure config = DalClientFactory.getDalConfigure();

        DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
        DalShardingStrategy strategy = dbSet.getStrategy();

        DalHints tmpHints = new DalHints();
        for (Integer index : pojos.keySet()) {
            Map<String, ?> fields = pojos.get(index);

            String shardId = tableShardId == null
                    ? strategy.locateTableShard(config, logicDbName, tableName, tmpHints.setFields(fields))
                    : tableShardId;

            Map<Integer, Map<String, ?>> pojosInShard = shuffled.get(shardId);
            if (pojosInShard == null) {
                pojosInShard = new LinkedHashMap<>();
                shuffled.put(shardId, pojosInShard);
            }

            pojosInShard.put(index, fields);
        }

        return shuffled;
    }

    // for DalHints.tableShardBy
    public static Map<String, List<?>> shuffleByTable(String logicDbName, String tableName, String tablerShardId,
            List<?> parameters) throws SQLException {
        Map<String, List<?>> shuffled = new HashMap<>();
        DalConfigure configure = DalClientFactory.getDalConfigure();
        DatabaseSet databaseSet = configure.getDatabaseSet(logicDbName);
        DalShardingStrategy strategy = databaseSet.getStrategy();

        for (int i = 0; i < parameters.size(); i++) {
            Object value = parameters.get(i);

            DalHints tempHints = new DalHints();
            String tableShardId = tablerShardId != null ? tablerShardId
                    : strategy.locateTableShard(configure, logicDbName, tableName, tempHints.setTableShardValue(value));
            List pojosInTableShard = shuffled.get(tableShardId);
            if (pojosInTableShard == null) {
                pojosInTableShard = new LinkedList();
                shuffled.put(tableShardId, pojosInTableShard);
            }

            pojosInTableShard.add(value);
        }

        return shuffled;
    }

    /**
     * Verify if shard id is already set for potential corss shard batch operation. This includes combined insert, batch
     * insert and batch delete. It will first check if sharding is enabled. Then detect if necessary sharding id can be
     * located. This applies to both db and table shard. If all meet, then allow the operation TODO do more analyze of
     * the logic here
     *
     * @param logicDbName
     * @param tableName
     * @param hints
     * @return if all sharding id can be located.
     * @throws SQLException
     */
    public static boolean isAlreadySharded(String logicDbName, String tableName, DalHints hints) throws SQLException {
        // For normal case, both DB and table sharding are not enabled
        if (!(isShardingEnabled(logicDbName) || isTableShardingEnabled(logicDbName, tableName)))
            return true;

        // Assume the out transaction already handle sharding logic
        // This may have potential issue if PD think they can do cross DB operation
        // TODO check here
        if (DalTransactionManager.isInTransaction())
            return true;

        hints.cleanUp();

        // Verify if DB shard is defined
        if (isShardingEnabled(logicDbName) && !locateShardId(logicDbName, hints))
            return false;

        // Verify if table shard is defined
        if (isTableShardingEnabled(logicDbName, tableName) && !locateTableShardId(logicDbName, tableName, hints))
            return false;

        return true;
    }

    public static void detectBulkTaskDistributedTransaction(String logicDbName, DalHints hints,
            List<Map<String, ?>> daoPojos) throws SQLException {
        if (!isShardingEnabled(logicDbName))
            return;

        Set<String> notNullShardIds =
                getNotNullShardIds(getPojosGroupedByShardId(logicDbName, hints.getShardId(), daoPojos, hints).keySet());
        if (notNullShardIds.size() > 1)
            logger.logEvent(DalLogTypes.DAL_VALIDATION, CROSSSHARD_BULKREQUEST, "");

        if (!DalTransactionManager.isInTransaction())
            return;

        if (notNullShardIds.size() > 1)
            logger.logEvent(DalLogTypes.DAL_VALIDATION, CROSSSHARD_BULKREQUEST_IN_TRANSACTION, "");
    }

    public static void detectDistributedTransaction(String logicDbName, DalHints hints, List<Map<String, ?>> daoPojos)
            throws SQLException {
        if (!isShardingEnabled(logicDbName))
            return;

        if (!DalTransactionManager.isInTransaction())
            return;

        String shardId = null;
        if (locateShardId(logicDbName, hints)) {
            shardId = hints.getShardId();
            isSameShard(shardId);
        } else {
            detectDistributedTransaction(getPojosGroupedByShardId(logicDbName, shardId, daoPojos, hints).keySet());
        }
    }

    public static void detectDistributedTransaction(Set<String> shardIds) throws SQLException {
        if (!DalTransactionManager.isInTransaction())
            return;

        if (shardIds == null)
            return;

        Set<String> notNullShardIds = getNotNullShardIds(shardIds);

        if (notNullShardIds.size() == 0)
            return;

        // Not allowed for distributed transaction
        if (notNullShardIds.size() > 1)
            throw new SQLException("Potential distributed operation detected in shards: " + notNullShardIds);

        String shardId = notNullShardIds.iterator().next();

        isSameShard(shardId);
    }

    private static Set<String> getNotNullShardIds(Set<String> shardIds) {
        Set<String> notNullShardIds = new HashSet<>();
        for (String key : shardIds) {
            if (key != null)
                notNullShardIds.add(key);
        }
        return notNullShardIds;
    }

    private static void isSameShard(String shardId) throws SQLException {
        try {
            if (Integer.valueOf(shardId).equals(Integer.valueOf(DalTransactionManager.getCurrentShardId())))
                return;
        } catch (Exception e) {

        }
        if (!shardId.equals(DalTransactionManager.getCurrentShardId()))
            throw new SQLException(
                    "Operation is not allowed in different database shard within current transaction. Current shardId: "
                            + DalTransactionManager.getCurrentShardId() + ". Requested shardId: " + shardId);
    }

    public static Set<String> getAllShards(String logicDbName) {
        return DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getAllShards();
    }

    public static Set<String> getAllTableShards(String logicDbName, String tableName) throws SQLException {
        return DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getAllTableShards(tableName);
    }

}
