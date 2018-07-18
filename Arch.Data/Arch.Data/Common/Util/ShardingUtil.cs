using Arch.Data.Common.constant;
using Arch.Data.Common.Enums;
using Arch.Data.DbEngine;
using Arch.Data.DbEngine.Sharding;
using Arch.Data.Orm.sql;
using System;
using System.Collections;
using System.Collections.Generic;

namespace Arch.Data.Common.Util
{
    /// 是否做了Sharding
    /// 
    /// 判断流程（需要考虑性能）：
    /// 1. IsNeedShard : 是否在Dal.config中配置了ShardingStrategy，如果是False，则IsShardByDb始终是False
    /// 2. IsShardByDb： 是否分库，即是否在Dal.config为每个DatabaseSet中配置了多个数据库
    /// 3. 如果传入TABLEID，则表名不相同
    /// 4. 如果传入SHARDID，则不需要自动计算
    ///     4.1 如果没有传入ShardID，且IsShardByDb为True，则自动计算ShardID
    ///     4.2 如果IsShardByDb为True，但是计算出来ShardID是NULL，则抛出异常
    class ShardingUtil
    {
        public static Boolean CheckKeyIsShardColumn(IShardingStrategy shardingStrategy, SqlTable table)
        {
            if (shardingStrategy == null || shardingStrategy.ShardColumns == null || shardingStrategy.ShardColumns.Count == 0)
                return false;
            if (table == null || table.PkColumn == null || table.PkColumns.Count == 0)
                return false;

            //only support one shard column now
            String shardColumn = shardingStrategy.ShardColumns[0];
            return String.Equals(shardColumn, table.PkColumns[0].Name, StringComparison.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Whether shard is enabled
        /// </summary>
        /// <param name="shardingStrategy"></param>
        /// <returns></returns>
        public static Boolean IsShardEnabled(IShardingStrategy shardingStrategy)
        {
            return shardingStrategy != null;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="shardingStrategy"></param>
        /// <returns></returns>
        private static Boolean IsShardByDb(IShardingStrategy shardingStrategy)
        {
            return shardingStrategy != null && shardingStrategy.ShardByDB;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="shardingStrategy"></param>
        /// <returns></returns>
        private static Boolean IsShardByTable(IShardingStrategy shardingStrategy)
        {
            return shardingStrategy != null && shardingStrategy.ShardByTable;
        }

        /// <summary>
        /// ShardByDB by default
        /// </summary>
        /// <param name="shardingStrategy"></param>
        /// <returns></returns>
        public static ShardingType GetShardingType(IShardingStrategy shardingStrategy)
        {
            Boolean shardByDb = IsShardByDb(shardingStrategy);
            Boolean shardByTable = IsShardByTable(shardingStrategy);

            if (shardByDb && shardByTable)
                return ShardingType.ShardByDBAndTable;
            if (shardByDb)
                return ShardingType.ShardByDB;
            if (shardByTable)
                return ShardingType.ShardByTable;

            return ShardingType.ShardByDB;
        }

        /// <summary>
        /// 从hints中提取ShardID
        /// </summary>
        /// <param name="hints"></param>
        /// <returns></returns>
        private static String GetShardIdByHints(IDictionary hints)
        {
            if (hints != null && hints.Contains(DALExtStatementConstant.SHARDID) && hints[DALExtStatementConstant.SHARDID] != null)
                return hints[DALExtStatementConstant.SHARDID].ToString();
            return null;
        }

        /// <summary>
        /// 从hints中提取TABLEID
        /// </summary>
        /// <param name="hints"></param>
        /// <returns></returns>
        private static String GetTableIdByHints(IDictionary hints)
        {
            if (hints != null && hints.Contains(DALExtStatementConstant.TABLEID) && hints[DALExtStatementConstant.TABLEID] != null)
                return hints[DALExtStatementConstant.TABLEID].ToString();
            return null;
        }

        /// <summary>
        /// 计算在哪个Shard执行操作
        /// </summary>
        /// <param name="shardingStrategy"></param>
        /// <param name="shardColumnValue"></param>
        /// <returns></returns>
        private static String CalculateShardId(IShardingStrategy shardingStrategy, IComparable shardColumnValue)
        {
            if (shardingStrategy == null || shardColumnValue == null)
                return null;
            return shardingStrategy.ComputeShardId(shardColumnValue);
        }

        public static Tuple<String, String> GetShardInfo<T>(String logicDbName, IShardingStrategy shardingStrategy, T entity, ICollection<SqlColumn> columns, StatementParameterCollection parameters, IDictionary hints)
        {
            String shardId = null;
            String tableId = null;
            Boolean shardEnabled = IsShardEnabled(shardingStrategy);
            if (!shardEnabled)
                return Tuple.Create<String, String>(shardId, tableId);

            shardId = GetShardId(logicDbName, shardingStrategy, entity, columns, parameters, hints);
            tableId = GetTableId(logicDbName, shardingStrategy, entity, columns, parameters, hints);

            if (String.IsNullOrEmpty(shardId) && String.IsNullOrEmpty(tableId))
                throw new DalException("Please provide shard information.");

            return Tuple.Create<String, String>(shardId, tableId);
        }

        private static String GetShardId<T>(String logicDbName, IShardingStrategy shardingStrategy,
            T entity, ICollection<SqlColumn> columns, StatementParameterCollection parameters, IDictionary hints)
        {
            String shardId = null;
            Boolean shardByDb = IsShardByDb(shardingStrategy);

            if (shardByDb)
            {
                shardId = GetShardIdByHints(hints);

                if (String.IsNullOrEmpty(shardId))
                {
                    IComparable shardColumnValue = shardingStrategy.GetShardColumnValue(logicDbName, entity, columns, parameters, hints);
                    shardId = CalculateShardId(shardingStrategy, shardColumnValue);
                }
            }

            return shardId;
        }

        private static String GetTableId<T>(String logicDbName, IShardingStrategy shardingStrategy,
            T entity, ICollection<SqlColumn> columns, StatementParameterCollection parameters, IDictionary hints)
        {
            String tableId = null;
            Boolean shardByTable = IsShardByTable(shardingStrategy);

            if (shardByTable)
            {
                tableId = GetTableIdByHints(hints);

                if (String.IsNullOrEmpty(tableId))
                {
                    IComparable shardColumnValue = shardingStrategy.GetShardColumnValue(logicDbName, entity, columns, parameters, hints);
                    tableId = CalculateShardId(shardingStrategy, shardColumnValue);
                }
            }

            return tableId;
        }

        #region Shuffled Items

        private static IDictionary<String, IList<T>> ShuffledByDb<T>(String logicDbName, IShardingStrategy shardingStrategy,
            IList<T> list, ICollection<SqlColumn> columns, IDictionary hints)
        {
            if (String.IsNullOrEmpty(logicDbName))
                return null;
            if (list == null || list.Count == 0)
                return null;
            var dict = new Dictionary<String, IList<T>>();

            foreach (var item in list)
            {
                String shardId = GetShardId(logicDbName, shardingStrategy, item, columns, null, hints);
                if (String.IsNullOrEmpty(shardId))
                    continue;

                if (!dict.ContainsKey(shardId))
                    dict.Add(shardId, new List<T>());
                dict[shardId].Add(item);
            }

            return dict;
        }

        private static IDictionary<String, IList<T>> ShuffledByTable<T>(String logicDbName, IShardingStrategy shardingStrategy,
            IList<T> list, ICollection<SqlColumn> columns, IDictionary hints)
        {
            if (String.IsNullOrEmpty(logicDbName))
                return null;
            if (list == null || list.Count == 0)
                return null;
            var dict = new Dictionary<String, IList<T>>();

            foreach (var item in list)
            {
                String tableId = GetTableId(logicDbName, shardingStrategy, item, columns, null, hints);
                if (String.IsNullOrEmpty(tableId))
                    continue;

                if (!dict.ContainsKey(tableId))
                    dict.Add(tableId, new List<T>());
                dict[tableId].Add(item);
            }

            return dict;
        }

        private static IDictionary<String, IDictionary<String, IList<T>>> ShuffledByDbTable<T>(String logicDbName, IShardingStrategy shardingStrategy,
            IList<T> list, ICollection<SqlColumn> columns, IDictionary hints)
        {
            if (String.IsNullOrEmpty(logicDbName))
                return null;
            if (list == null || list.Count == 0)
                return null;

            var dict = new Dictionary<String, IDictionary<String, IList<T>>>();

            foreach (var item in list)
            {
                String shardId = GetShardId(logicDbName, shardingStrategy, item, columns, null, hints);
                String tableId = GetTableId(logicDbName, shardingStrategy, item, columns, null, hints);

                if (!dict.ContainsKey(shardId))
                    dict.Add(shardId, new Dictionary<String, IList<T>>());
                if (!dict[shardId].ContainsKey(tableId))
                    dict[shardId].Add(tableId, new List<T>());
                dict[shardId][tableId].Add(item);
            }

            return dict;
        }

        #endregion Shuffled Items

        public static IList<Statement> GetShardStatement(String logicDbName, IShardingStrategy shardingStrategy,
            StatementParameterCollection parameters, IDictionary hints, Func<IDictionary, Statement> func)
        {
            IList<Statement> statements;
            if (shardingStrategy == null)
                return null;
            var shardingType = GetShardingType(shardingStrategy);

            if (shardingType != ShardingType.ShardByDBAndTable)
            {
                IList<String> shards = null;

                //Get shards from hints
                if (hints != null)
                {
                    IList<String> temp = null;

                    if (shardingType == ShardingType.ShardByDB)
                    {
                        if (hints.Contains(DALExtStatementConstant.SHARD_IDS))
                        {
                            temp = hints[DALExtStatementConstant.SHARD_IDS] as List<String>;
                        }
                        else if (hints.Contains(DALExtStatementConstant.SHARDID))
                        {
                            temp = new List<String> { hints[DALExtStatementConstant.SHARDID] as String };
                        }
                    }
                    else if (shardingType == ShardingType.ShardByTable)
                    {
                        if (hints.Contains(DALExtStatementConstant.TABLE_IDS))
                        {
                            temp = hints[DALExtStatementConstant.TABLE_IDS] as List<String>;
                        }
                        else if (hints.Contains(DALExtStatementConstant.TABLEID))
                        {
                            temp = new List<String> { hints[DALExtStatementConstant.TABLEID] as String };
                        }
                    }

                    if (temp != null)
                        shards = temp;
                }

                //Get shards from parameters
                if (shards == null)
                {
                    if (parameters != null)
                    {
                        if (shardingType == ShardingType.ShardByDB)
                        {
                            String shardId = GetShardId<Object>(logicDbName, shardingStrategy, null, null, parameters, hints);
                            if (!String.IsNullOrEmpty(shardId))
                                shards = new List<String> { shardId };
                        }
                        else if (shardingType == ShardingType.ShardByTable)
                        {
                            String tableId = GetTableId<Object>(logicDbName, shardingStrategy, null, null, parameters, hints);
                            if (!String.IsNullOrEmpty(tableId))
                                shards = new List<String> { tableId };
                        }
                    }
                }

                if (shards == null)
                    throw new DalException("Please provide shard information.");

                //Build statements
                statements = new List<Statement>();

                foreach (var item in shards)
                {
                    var newHints = HintsUtil.CloneHints(hints);

                    switch (shardingType)
                    {
                        case ShardingType.ShardByDB:
                            newHints[DALExtStatementConstant.SHARDID] = item;
                            break;
                        case ShardingType.ShardByTable:
                            newHints[DALExtStatementConstant.TABLEID] = item;
                            break;
                    }

                    Statement statement = func.Invoke(newHints);
                    statements.Add(statement);
                }
            }
            else
            {
                statements = new List<Statement>();
                IDictionary<String, IList<String>> shardDict = null;
                if (hints != null && hints.Contains(DALExtStatementConstant.SHARD_TABLE_DICT))
                    shardDict = hints[DALExtStatementConstant.SHARD_TABLE_DICT] as IDictionary<String, IList<String>>;

                if (shardDict == null)
                {
                    var newHints = HintsUtil.CloneHints(hints);
                    newHints[DALExtStatementConstant.SHARDID] = GetShardIdByHints(hints);
                    newHints[DALExtStatementConstant.TABLEID] = GetTableIdByHints(hints);
                    Statement statement = func.Invoke(newHints);
                    statements.Add(statement);
                }
                else
                {
                    foreach (var shard in shardDict)
                    {
                        foreach (var table in shard.Value)
                        {
                            var newHints = HintsUtil.CloneHints(hints);
                            newHints[DALExtStatementConstant.SHARDID] = shard.Key;
                            newHints[DALExtStatementConstant.TABLEID] = table;
                            Statement statement = func.Invoke(newHints);
                            statements.Add(statement);
                        }
                    }
                }
            }

            return statements;
        }

        public static IList<Statement> GetShardStatementByEntity<T>(String logicDbName, IShardingStrategy shardingStrategy,
            IList<T> list, SqlTable table, IDictionary hints, Func<IList<T>, IDictionary, Statement> func) where T : class, new()
        {
            if (String.IsNullOrEmpty(logicDbName))
                return null;
            if (list == null || list.Count == 0)
                return null;
            if (table == null)
                return null;

            var statements = new List<Statement>();
            var shardingType = GetShardingType(shardingStrategy);

            if (shardingType == ShardingType.ShardByDB)
            {
                var dict = ShuffledByDb(logicDbName, shardingStrategy, list, table.ColumnList, hints);

                if (dict != null && dict.Count > 0)
                {
                    foreach (var item in dict)
                    {
                        var newHints = HintsUtil.CloneHints(hints);
                        newHints[DALExtStatementConstant.SHARDID] = item.Key;
                        Statement statement = func.Invoke(item.Value, newHints);
                        statements.Add(statement);
                    }
                }
            }
            else if (shardingType == ShardingType.ShardByTable)
            {
                var dict = ShuffledByTable(logicDbName, shardingStrategy, list, table.ColumnList, hints);

                if (dict != null && dict.Count > 0)
                {
                    foreach (var item in dict)
                    {
                        var newHints = HintsUtil.CloneHints(hints);
                        newHints[DALExtStatementConstant.TABLEID] = item.Key;
                        Statement statement = func.Invoke(item.Value, newHints);
                        statements.Add(statement);
                    }
                }
            }
            else
            {
                var dict = ShuffledByDbTable(logicDbName, shardingStrategy, list, table.ColumnList, hints);

                if (dict != null && dict.Count > 0)
                {
                    foreach (var item in dict)
                    {
                        foreach (var item2 in item.Value)
                        {
                            var newHints = HintsUtil.CloneHints(hints);
                            newHints[DALExtStatementConstant.SHARDID] = item.Key;
                            newHints[DALExtStatementConstant.TABLEID] = item2.Key;
                            Statement statement = func.Invoke(item2.Value, newHints);
                            statements.Add(statement);
                        }
                    }
                }
            }

            return statements;
        }

    }
}