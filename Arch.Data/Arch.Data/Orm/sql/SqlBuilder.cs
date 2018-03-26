using Arch.Data.Common.constant;
using Arch.Data.Common.Enums;
using Arch.Data.Common.Util;
using Arch.Data.DbEngine;
using Arch.Data.DbEngine.Sharding;
using Arch.Data.Orm.Dialect;
using Arch.Data.Orm.partially;
using Arch.Data.Utility;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.Text;

namespace Arch.Data.Orm.sql
{
    public class SqlBuilder
    {
        /// <summary>
        /// "INSERT INTO {0}({1}) VALUES ({2});"
        /// </summary>
        private const String SqlInsertTemplate = @"INSERT INTO {0}({1}) VALUES {2};";

        /// <summary>
        /// "REPLACE INTO {0}({1}) VALUES ({2});"
        /// </summary>
        private const String SqlReplaceTemplate = @"REPLACE INTO {0}({1}) VALUES {2};";

        /// <summary>
        /// "DELETE FROM {0} WHERE {1};"
        /// </summary>
        private const String SqlDeleteTemplate = @"DELETE FROM {0} WHERE {1};";

        /// <summary>
        /// "UPDATE {0} SET {1} WHERE {2};"
        /// </summary>
        private const String SqlUpdateTemplate = @"UPDATE {0} SET {1} WHERE {2};";

        /// <summary>
        /// "SELECT {0} FROM {1}"
        /// </summary>
        private const String SqlSelectTemplate = @"SELECT {0} FROM {1};";

        /// <summary>
        /// "SELECT {0} FROM {1} WHERE {2}"
        /// </summary>
        private const String SqlSelectWhereTemplate = @"SELECT {0} FROM {1} WHERE {2};";

        private const String And = " AND ";

        private const String Comma = ",";

        #region Methods

        private static String GetColumnsName(IEnumerable<IColumn> columns, IDbDialect dbDialect)
        {
            if (columns == null) return String.Empty;
            List<IColumn> list = columns.ToList();
            return String.Join(Comma, list.Select(p => dbDialect.Quote(p.Name)));
        }

        private static IDictionary<String, IColumn> FilterColumnsDict<T>(IEnumerable<IColumn> columns, IDbDialect dbDialect, T entity,
            Boolean columnReadable, Boolean ignoreIdColumn, Boolean ignorePkColumn, Boolean ignoreNullValue, Boolean quote, Boolean ignoreColumn, out IList<IColumn> pkColumns) where T : class, new()
        {
            IDictionary<String, IColumn> columnsDict = new Dictionary<String, IColumn>();
            IList<IColumn> columnList = new List<IColumn>();

            if (columns == null)
            {
                pkColumns = columnList;
                return columnsDict;
            }

            List<IColumn> list = columns.ToList();

            foreach (var item in list)
            {
                if (item.IsPK) columnList.Add(item);

                if (columnReadable && !item.IsReadable) continue;
                if (ignoreIdColumn && item.IsID) continue;
                if (ignorePkColumn && item.IsPK) continue;

                if (ignoreNullValue)
                {
                    if (entity == null) continue;
                    Object value = item.Data.Read(entity);
                    if (value == null) continue;
                }

                if (ignoreColumn && item.IsIgnored) continue;

                String key = quote ? dbDialect.Quote(item.Name.ToUpper()) : item.Name.ToUpper();
                columnsDict[key] = item;
            }

            pkColumns = columnList;
            return columnsDict;
        }

        private static IDictionary<String, IColumn> GetNonOptimisticColumnsDict(IDictionary<String, IColumn> columnsDict,
            out IDictionary<String, IColumn> optimisticLock, out IDictionary<String, IColumn> datetimeLock, out IDictionary<String, IColumn> versionLock)
        {
            IDictionary<String, IColumn> nonOptimisticDict = new Dictionary<String, IColumn>();
            IDictionary<String, IColumn> optimisticDict = new Dictionary<String, IColumn>();
            IDictionary<String, IColumn> datetimeDict = new Dictionary<String, IColumn>();
            IDictionary<String, IColumn> versionDict = new Dictionary<String, IColumn>();

            if (columnsDict == null)
            {
                optimisticLock = optimisticDict;
                datetimeLock = datetimeDict;
                versionLock = versionDict;
                return nonOptimisticDict;
            }

            foreach (var item in columnsDict)
            {
                if (item.Value.IsTimestampLock)
                {
                    datetimeDict[item.Key] = item.Value;
                    optimisticDict[item.Key] = item.Value;
                }
                else if (item.Value.IsVersionLock)
                {
                    versionDict[item.Key] = item.Value;
                    optimisticDict[item.Key] = item.Value;
                }
                else
                {
                    nonOptimisticDict[item.Key] = item.Value;
                }
            }

            optimisticLock = optimisticDict;
            datetimeLock = datetimeDict;
            versionLock = versionDict;
            return nonOptimisticDict;
        }

        private static IList<String> GetClauseList(IEnumerable<IColumn> columns, IDbDialect dbDialect, Boolean columnReadable, Boolean setDatetimeLock, Boolean parameterPrefix)
        {
            IList<String> clauseList = new List<String>();
            if (columns == null)
                return clauseList;

            foreach (var item in columns)
            {
                if ((columnReadable && !item.IsReadable) || item.IsIgnored) continue;

                StringBuilder sb = new StringBuilder().Append(dbDialect.Quote(item.Name)).Append("=");

                if (!setDatetimeLock)
                {
                    if (!parameterPrefix)
                    {
                        sb.Append(dbDialect.QuoteParameter(item.Ordinal.ToString()));
                    }
                    else
                    {
                        sb.Append(dbDialect.QuotePrefixParameter(item.Ordinal.ToString()));
                    }
                }
                else
                {
                    sb.Append(item.TimestampExpression);
                }

                clauseList.Add(sb.ToString());
            }

            return clauseList;
        }

        private static String GetColumnsParameterName(IEnumerable<IColumn> parameters, IDbDialect dbDialect)
        {
            if (parameters == null)
                return String.Empty;
            return String.Join(Comma, parameters.Select(p => dbDialect.QuoteParameter(p.Ordinal.ToString())));
        }

        private static IList<String> GetColumnsParameterNameList(IEnumerable<IColumn> parameters, Int32 count, IDbDialect dbDialect)
        {
            IList<String> result = new List<String>();
            if (parameters == null || count < 1)
                return result;

            List<IColumn> paramList = parameters.ToList();
            Int32 index = 0;

            while (index < count)
            {
                String temp = String.Join(Comma, paramList.Select(p => dbDialect.QuoteParameter(String.Format("{0}_{1}", index.ToString(), p.Ordinal.ToString()))));
                result.Add(String.Format("({0})", temp));
                index++;
            }

            return result;
        }

        private static String GetSchemeTableName(String schema, String tableId, String table, IDbDialect dbDialect)
        {
            if (String.IsNullOrEmpty(table))
                return String.Empty;

            String schemaName = !String.IsNullOrEmpty(schema) ? String.Concat(dbDialect.Quote(schema), ".") : String.Empty;
            String tableName = dbDialect.Quote(String.IsNullOrEmpty(tableId) ? table : String.Format(table, tableId));
            return String.Concat(schemaName, tableName);
        }

        private static String GetLockType(IDictionary extendedParameters, IDbDialect dbDialect)
        {
            return (extendedParameters != null && extendedParameters.Contains(DALExtStatementConstant.LOCK)) ?
                dbDialect.WithLock(extendedParameters[DALExtStatementConstant.LOCK].ToString()) : dbDialect.WithLock(null);
        }

        private static String GetIdentityQuerySql(IDbDialect dbDialect)
        {
            return dbDialect != null ? dbDialect.IdentitySelectString : String.Empty;
        }

        private static void BuildParameters<T>(StatementParameterCollection parameters, IEnumerable<IColumn> columns, T item, IDbDialect dbDialect, Boolean isVersionLock, Boolean parameterPrefix)
        {
            BuildBulkParameters(parameters, columns, new[] { item }, dbDialect, isVersionLock, false, parameterPrefix);
        }

        private static void BuildBulkParameters<T>(StatementParameterCollection parameters, IEnumerable<IColumn> columns, IEnumerable<T> items, IDbDialect dbDialect,
            Boolean isVersionLock, Boolean bulkPattern, Boolean parameterPrefix)
        {
            if (parameters == null)
                parameters = new StatementParameterCollection();
            if (columns == null || items == null) return;

            Int32 index = 0;
            List<IColumn> list = columns.ToList();

            foreach (var item in items)
            {
                if (item == null) continue;

                foreach (var column in list)
                {
                    DbType dbType = SqlUtils.GetDbType(column);
                    Object value = column.Data.Read(item);

                    if (isVersionLock)
                    {
                        if (dbType == DbType.Int64 || dbType == DbType.UInt64)
                        {
                            value = (Int64)value + 1;
                        }
                        else if (dbType == DbType.Int32 || dbType == DbType.UInt32)
                        {
                            value = (Int32)value + 1;
                        }
                        else
                        {
                            value = (Int16)value + 1;
                        }
                    }

                    String parameterName = !bulkPattern ? column.Ordinal.ToString() : String.Format("{0}_{1}", index.ToString(), column.Ordinal.ToString());

                    parameters.Add(new StatementParameter
                    {
                        Name = parameterPrefix ? dbDialect.QuotePrefixParameter(parameterName) : dbDialect.QuoteParameter(parameterName),
                        Value = value,
                        DbType = dbType,
                        Direction = ParameterDirection.Input,
                        Size = column.Length
                    });
                }

                index++;
            }
        }

        private static void BuildSpParameters<T>(StatementParameterCollection parameters, IEnumerable<IColumn> columns, T item, IDbDialect dbDialect)
        {
            if (parameters == null || columns == null || item == null) return;

            foreach (var column in columns)
            {
                StatementParameter parameter = new StatementParameter
                {
                    DbType = SqlUtils.GetDbType(column),
                    Direction = ParameterDirection.Input,
                    Name = dbDialect.QuoteParameter(column.Name),
                    Value = column.Data.Read(item),
                    Size = column.Length
                };

                parameters.Add(parameter);
            }
        }

        #endregion

        #region Statement

        private static Statement GetStatement(String logicDbName, Arch.Data.Common.Enums.StatementType statementType, OperationType operationType, SqlStatementType sqlType, IDictionary hints, String shardId)
        {
            return new Statement
            {
                DatabaseSet = logicDbName,
                StatementType = statementType,
                OperationType = operationType,
                Hints = hints,
                ShardID = shardId,
                SqlOperationType = sqlType
            };
        }

        #endregion

        /// <summary>
        /// GetAllStatement
        /// </summary>
        /// <returns></returns>
        public static Statement GetAllStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints, OperationType? operationType = null)
        {
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            var tuple = ShardingUtil.GetShardInfo<Object>(logicDbName, shardingStrategy, null, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, operationType ?? OperationType.Default, SqlStatementType.SELECT, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat(SqlSelectTemplate,
                GetColumnsName(table.Columns, dbDialect),
                String.Concat(GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect), GetLockType(hints, dbDialect)));
            statement.StatementText = sb.ToString();

            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetFindByKeyStatement
        /// </summary>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="hints"></param>
        /// <param name="key"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetFindByKeyStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints, Object key, OperationType? operationType = null)
        {
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            var tuple = ShardingUtil.GetShardInfo<Object>(logicDbName, shardingStrategy, null, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, operationType ?? OperationType.Default, SqlStatementType.SELECT, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            FilterColumnsDict<Object>(table.Columns, dbDialect, null, false, false, false, false, true, false, out pkColumns);
            if (pkColumns == null)
                throw new DalException("Please specify Pk attribute.");

            StringBuilder sb = new StringBuilder();
            sb.AppendFormat(SqlSelectWhereTemplate,
                GetColumnsName(table.Columns, dbDialect),
                String.Concat(GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect), GetLockType(hints, dbDialect)),
                String.Join(And, GetClauseList(pkColumns, dbDialect, true, false, false)));

            statement.StatementText = sb.ToString();

            foreach (var column in pkColumns)
            {
                statement.Parameters.Add(new StatementParameter
                {
                    Name = dbDialect.QuoteParameter(column.Ordinal.ToString()),
                    Value = key,
                    DbType = SqlUtils.GetDbType(column),
                    Direction = ParameterDirection.Input,
                    Size = column.Length
                });
            }

            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetInsertSqlStatement
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="item"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="hints"></param>
        /// <param name="isCompositeKey"></param>
        /// <returns></returns>
        public static Statement GetInsertSqlStatement<T>(SqlTable table, T item, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints, Boolean isCompositeKey) where T : class, new()
        {
            if (item == null)
                throw new DalException("Entity can't be null.");
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            Boolean ignoreNullValue = ParameterUtility.IgnoreNullValue(hints);
            var tuple = ShardingUtil.GetShardInfo(logicDbName, shardingStrategy, item, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.INSERT, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> columnsDict = FilterColumnsDict(table.Columns, dbDialect, item, true, true, false, ignoreNullValue, true, true, out pkColumns);
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat(SqlInsertTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                GetColumnsName(columnsDict.Values, dbDialect),
                String.Format("({0})", GetColumnsParameterName(columnsDict.Values, dbDialect)));

            if (!isCompositeKey && table.Identity != null)
            {
                sb.Append(GetIdentityQuerySql(dbDialect));

                StatementParameter parameter = new StatementParameter
                {
                    DbType = SqlUtils.GetDbType(table.Identity),
                    Direction = ParameterDirection.Output,
                    Name = dbDialect.QuoteParameter("ID"),
                    Size = table.Identity.Length
                };

                statement.Parameters.Add(parameter);
            }

            statement.StatementText = sb.ToString();
            BuildParameters(statement.Parameters, columnsDict.Values, item, dbDialect, false, false);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetBulkInsertSqlStatement
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="items"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="hints"></param>
        /// <returns></returns>
        public static Statement GetBulkInsertSqlStatement<T>(SqlTable table, IList<T> items, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints) where T : class, new()
        {
            Int32 count = items.Count;
            if (items == null || count == 0)
                throw new DalException("Entity can't be null.");
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            var tuple = ShardingUtil.GetShardInfo<Object>(logicDbName, shardingStrategy, null, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.INSERT, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> columnsDict = FilterColumnsDict<Object>(table.Columns, dbDialect, null, true, true, false, false, true, true, out pkColumns);
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat(SqlInsertTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                GetColumnsName(columnsDict.Values, dbDialect),
                String.Join(Comma, GetColumnsParameterNameList(columnsDict.Values, count, dbDialect)));

            statement.StatementText = sb.ToString();
            BuildBulkParameters(statement.Parameters, columnsDict.Values, items, dbDialect, false, true, false);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="item"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="hints"></param>
        /// <param name="isComplexPk"></param>
        /// <returns></returns>
        public static Statement GetReplaceSqlStatement<T>(SqlTable table, T item, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints, Boolean isComplexPk) where T : class, new()
        {
            if (item == null)
                throw new DalException("Entity can't be null.");
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            Boolean ignoreNullValue = ParameterUtility.IgnoreNullValue(hints);
            var tuple = ShardingUtil.GetShardInfo(logicDbName, shardingStrategy, item, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.UPDATE, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> columnsDict = FilterColumnsDict(table.Columns, dbDialect, item, true, false, false, ignoreNullValue, true, true, out pkColumns);
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat(SqlReplaceTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                GetColumnsName(columnsDict.Values, dbDialect),
                String.Format("({0})", GetColumnsParameterName(columnsDict.Values, dbDialect)));

            if (!isComplexPk && table.Identity != null)
            {
                sb.Append(GetIdentityQuerySql(dbDialect));

                StatementParameter parameter = new StatementParameter
                {
                    DbType = SqlUtils.GetDbType(table.Identity),
                    Direction = ParameterDirection.Output,
                    Name = dbDialect.QuoteParameter("ID"),
                    Size = table.Identity.Length
                };

                statement.Parameters.Add(parameter);
            }

            statement.StatementText = sb.ToString();
            BuildParameters(statement.Parameters, columnsDict.Values, item, dbDialect, false, false);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="item"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="replacePartial"></param>
        /// <param name="hints"></param>
        /// <param name="isComplexPk"></param>
        /// <returns></returns>
        public static Statement GetReplacePartialSqlStatement<T>(SqlTable table, T item, String logicDbName, IShardingStrategy shardingStrategy, IReplacePartial<T> replacePartial, IDictionary hints, Boolean isComplexPk) where T : class, new()
        {
            if (item == null)
                throw new DalException("Entity can't be null.");
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            var partial = replacePartial as ReplacePartial<T>;
            if (partial == null)
                throw new DalException("ReplacePartial can't be null.");

            var replaceFields = partial.ReplaceFields as Queue<String>;
            if (replaceFields == null)
                throw new DalException("ReplaceFields of ReplacePartial can't be null.");

            Boolean ignoreNullValue = ParameterUtility.IgnoreNullValue(hints);
            var tuple = ShardingUtil.GetShardInfo(logicDbName, shardingStrategy, item, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.UPDATE, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            var replaceColumns = table.Columns.Where(p => replaceFields.Contains(p.Name.ToUpper()));     //to be optimized for seeking performance
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> columnsDict = FilterColumnsDict(replaceColumns, dbDialect, item, true, false, false, ignoreNullValue, true, true, out pkColumns);
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat(SqlReplaceTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                GetColumnsName(columnsDict.Values, dbDialect),
                String.Format("({0})", GetColumnsParameterName(columnsDict.Values, dbDialect)));

            if (!isComplexPk && table.Identity != null)
            {
                sb.Append(GetIdentityQuerySql(dbDialect));

                StatementParameter parameter = new StatementParameter
                {
                    DbType = SqlUtils.GetDbType(table.Identity),
                    Direction = ParameterDirection.Output,
                    Name = dbDialect.QuoteParameter("ID"),
                    Size = table.Identity.Length
                };

                statement.Parameters.Add(parameter);
            }

            statement.StatementText = sb.ToString();
            BuildParameters(statement.Parameters, columnsDict.Values, item, dbDialect, false, false);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetBulkReplaceSqlStatement
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="items"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="hints"></param>
        /// <returns></returns>
        public static Statement GetBulkReplaceSqlStatement<T>(SqlTable table, IList<T> items, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints) where T : class, new()
        {
            Int32 count = items.Count;
            if (items == null || count == 0)
                throw new DalException("Entity can't be null.");
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            var tuple = ShardingUtil.GetShardInfo<Object>(logicDbName, shardingStrategy, null, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.UPDATE, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> columnsDict = FilterColumnsDict<Object>(table.Columns, dbDialect, null, true, false, false, false, true, true, out pkColumns);   //Filter ignoreNullValue
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat(SqlReplaceTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                GetColumnsName(columnsDict.Values, dbDialect),
                String.Join(Comma, GetColumnsParameterNameList(columnsDict.Values, count, dbDialect)));

            statement.StatementText = sb.ToString();
            BuildBulkParameters(statement.Parameters, columnsDict.Values, items, dbDialect, false, true, false);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetUpdateSqlStatement
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="item"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="hints"></param>
        /// <returns></returns>
        public static Statement GetUpdateSqlStatement<T>(SqlTable table, T item, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints) where T : class, new()
        {
            if (item == null)
                throw new DalException("Entity can't be null.");
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");
            if (table.PkColumns == null || table.PkColumns.Count == 0)
                throw new DalException("Please specify PK attribute.");

            Boolean ignoreNullValue = ParameterUtility.IgnoreNullValue(hints);
            var tuple = ShardingUtil.GetShardInfo(logicDbName, shardingStrategy, item, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.UPDATE, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> columnsDict = FilterColumnsDict(table.Columns, dbDialect, item, true, true, true, ignoreNullValue, true, true, out pkColumns);    //Set clause,filter ID,PK Columns
            IDictionary<String, IColumn> optimisticDict;
            IDictionary<String, IColumn> datetimeDict;
            IDictionary<String, IColumn> versionDict;
            IDictionary<String, IColumn> nonOptimisticDict = GetNonOptimisticColumnsDict(columnsDict, out optimisticDict, out datetimeDict, out versionDict);
            //Set clause
            List<String> setClauseList = new List<String>();
            setClauseList.AddRange(GetClauseList(nonOptimisticDict.Values, dbDialect, true, false, false));
            setClauseList.AddRange(GetClauseList(datetimeDict.Values, dbDialect, true, true, false));
            setClauseList.AddRange(GetClauseList(versionDict.Values, dbDialect, true, false, false));

            //Where clause
            List<String> whereClauseList = new List<String>();
            whereClauseList.AddRange(GetClauseList(table.PkColumns, dbDialect, true, false, true));
            whereClauseList.AddRange(GetClauseList(optimisticDict.Values, dbDialect, true, false, true));

            StringBuilder sb = new StringBuilder();
            sb.Append(dbDialect.QuoteOpenOpName(String.Empty));
            sb.AppendFormat(SqlUpdateTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                String.Join(Comma, setClauseList),
                String.Join(And, whereClauseList));
            sb.Append(dbDialect.QuoteCloseOpName());
            statement.StatementText = sb.ToString();

            //Parameters
            //Set
            BuildParameters(statement.Parameters, nonOptimisticDict.Values, item, dbDialect, false, false);
            BuildParameters(statement.Parameters, versionDict.Values, item, dbDialect, true, false);
            //Where
            BuildParameters(statement.Parameters, table.PkColumns, item, dbDialect, false, true);
            BuildParameters(statement.Parameters, optimisticDict.Values, item, dbDialect, false, true);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetUpdatePartialSqlStatement
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="item"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="updatePartial"></param>
        /// <param name="hints"></param>
        /// <returns></returns>
        public static Statement GetUpdatePartialSqlStatement<T>(SqlTable table, T item, string logicDbName, IShardingStrategy shardingStrategy, IUpdatePartial<T> updatePartial, IDictionary hints) where T : class, new()
        {
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");
            if (item == null)
                throw new DalException("Entity can't be null.");

            var partially = updatePartial as UpdatePartial<T>;
            if (partially == null)
                throw new DalException("IUpdatePartial can't be null.");

            var setFields = partially.SetFields;
            var whereFields = partially.WhereConditions;
            if (setFields == null || whereFields == null)
                throw new DalException("Expression of IUpdatePartial can't be null.");

            ISet<String> sets = new HashSet<String>();
            ISet<String> wheres = new HashSet<String>();
            foreach (var set in setFields)
            {
                sets.Add(set.ToUpper());
            }
            foreach (var where in whereFields)
            {
                wheres.Add(where.ToUpper());
            }

            Boolean ignoreNullValue = ParameterUtility.IgnoreNullValue(hints);
            var tuple = ShardingUtil.GetShardInfo(logicDbName, shardingStrategy, item, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.UPDATE, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> filteredSetColumns = FilterColumnsDict(table.Columns, dbDialect, item, true, true, true, ignoreNullValue, false, true, out pkColumns);
            var setColumnsDict = filteredSetColumns.Where(p => sets.Contains(p.Key)).ToDictionary(p => p.Key, p => p.Value);
            IDictionary<String, IColumn> optimisticDict;
            IDictionary<String, IColumn> datetimeDict;
            IDictionary<String, IColumn> versionDict;
            IDictionary<String, IColumn> nonOptimisticDict = GetNonOptimisticColumnsDict(setColumnsDict, out optimisticDict, out datetimeDict, out versionDict);

            //Set Clause
            List<String> setClauseList = new List<String>();
            setClauseList.AddRange(GetClauseList(nonOptimisticDict.Values, dbDialect, true, false, false));
            setClauseList.AddRange(GetClauseList(datetimeDict.Values, dbDialect, true, true, false));
            setClauseList.AddRange(GetClauseList(versionDict.Values, dbDialect, true, false, false));

            //Where Clause
            IList<IColumn> wherePkColumns;
            IDictionary<String, IColumn> filteredWhereColumns = FilterColumnsDict(table.Columns, dbDialect, item, true, false, false, ignoreNullValue, false, false, out wherePkColumns);
            IDictionary<String, IColumn> whereColumnsDict = filteredWhereColumns.Where(p => wheres.Contains(p.Key)).ToDictionary(p => p.Key, p => p.Value);
            IList<String> whereClauseList = GetClauseList(whereColumnsDict.Values, dbDialect, true, false, true);

            StringBuilder sb = new StringBuilder();
            sb.Append(dbDialect.QuoteOpenOpName(String.Empty));
            sb.AppendFormat(SqlUpdateTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                String.Join(Comma, setClauseList),
                String.Join(And, whereClauseList));
            sb.Append(dbDialect.QuoteCloseOpName());
            statement.StatementText = sb.ToString();

            //Parameters
            //Set
            BuildParameters(statement.Parameters, nonOptimisticDict.Values, item, dbDialect, false, false);
            BuildParameters(statement.Parameters, versionDict.Values, item, dbDialect, true, false);
            //Where
            BuildParameters(statement.Parameters, whereColumnsDict.Values, item, dbDialect, false, true);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetDeleteSqlStatement
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="item"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="hints"></param>
        /// <returns></returns>
        public static Statement GetDeleteSqlStatement<T>(SqlTable table, T item, String logicDbName, IShardingStrategy shardingStrategy, IDictionary hints) where T : class, new()
        {
            if (item == null)
                throw new DalException("Entity can't be null.");
            if (logicDbName == null)
                throw new DalException("Please specify databaseSet.");
            if (table.PkColumns == null || table.PkColumns.Count == 0)
                throw new DalException("Please specify PK attribute.");

            var tuple = ShardingUtil.GetShardInfo(logicDbName, shardingStrategy, item, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, OperationType.Write, SqlStatementType.DELETE, hints, tuple.Item1);
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<String> clauseList = GetClauseList(table.PkColumns, dbDialect, true, false, false);
            StringBuilder sb = new StringBuilder();
            sb.Append(dbDialect.QuoteOpenOpName(String.Empty));
            sb.AppendFormat(SqlDeleteTemplate,
                GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect),
                String.Join(And, clauseList));
            sb.Append(dbDialect.QuoteCloseOpName());
            statement.StatementText = sb.ToString();
            BuildParameters(statement.Parameters, table.PkColumns, item, dbDialect, false, false);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetSqlStatement
        /// </summary>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extendedParameters"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetSqlStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String sql, StatementParameterCollection parameters, IDictionary extendedParameters, OperationType? operationType = null)
        {
            return GetDefaultSqlStatement(table, logicDbName, shardingStrategy, sql, parameters, extendedParameters, SqlStatementType.SELECT, operationType ?? OperationType.Default);
        }

        /// <summary>
        /// GetScalarStatement
        /// </summary>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extendedParameters"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetScalarStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String sql, StatementParameterCollection parameters, IDictionary extendedParameters, OperationType? operationType = null)
        {
            return GetDefaultSqlStatement(table, logicDbName, shardingStrategy, sql, parameters, extendedParameters, SqlStatementType.SELECT, operationType ?? OperationType.Default);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extendedParameters"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetNonQueryStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String sql, StatementParameterCollection parameters, IDictionary extendedParameters, OperationType? operationType = null)
        {
            return GetDefaultSqlStatement(table, logicDbName, shardingStrategy, sql, parameters, extendedParameters, SqlStatementType.UNKNOWN, operationType ?? OperationType.Write);
        }

        private static Statement GetDefaultSqlStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String sql, StatementParameterCollection parameters, IDictionary hints, SqlStatementType sqlType, OperationType? operationType = null)
        {
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");
            if (String.IsNullOrEmpty(sql))
                throw new DalException("Please specify sql.");

            var tuple = ShardingUtil.GetShardInfo<Object>(logicDbName, shardingStrategy, null, table.ColumnList, parameters, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.Sql, operationType ?? OperationType.Default, sqlType, hints, tuple.Item1);
            statement.StatementText = GetSql(sql, tuple.Item2);
            statement.Parameters = parameters;
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        private static String GetSql(String sql, String tableId)
        {
            return String.IsNullOrEmpty(tableId) ? sql : String.Format(sql, tableId);
        }

        #region GetQueryStatment

        /// <summary>
        /// 
        /// </summary>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="query"></param>
        /// <param name="hints"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetQueryStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            IQuery query, IDictionary hints, OperationType? operationType = null)
        {
            var sqlQuery = query as SqlQuery;
            if (sqlQuery == null)
                throw new DalException("IQuery can't be null.");

            var tuple = ShardingUtil.GetShardInfo<Object>(logicDbName, shardingStrategy, null, table.ColumnList, null, hints);
            Statement statement = new Statement
            {
                DatabaseSet = logicDbName,
                StatementType = Arch.Data.Common.Enums.StatementType.Sql,
                OperationType = operationType ?? OperationType.Default,
                Hints = hints,
                ShardID = tuple.Item1,
                TableName = table.Name,
                SqlOperationType = SqlStatementType.SELECT
            };

            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            StringBuilder sb = new StringBuilder();
            sb.Append(sqlQuery.PagingPrefix(dbDialect));    //Prefix for SQL Server paging

            if (sb.Length == 0) sb.Append("SELECT ");
            sb.Append(sqlQuery.PrepareLimitPrefix(dbDialect));
            sb.Append(GetColumnsName(table.Columns, dbDialect));
            sb.Append(" FROM ");
            sb.Append(GetSchemeTableName(table.Schema, tuple.Item2, table.Name, dbDialect));
            sb.Append(GetLockType(hints, dbDialect));

            //todo:optimize
            sb.Append(" ").Append(sqlQuery.GetSql(table, dbDialect.OpenQuote, dbDialect.CloseQuote));
            sb.Append(sqlQuery.PrepareLimitSuffix(dbDialect));
            sb.Append(sqlQuery.PagingSuffix(dbDialect, table.ColumnList));
            sb.Append(";");

            statement.StatementText = sb.ToString();
            sqlQuery.SetParameters(table, statement.Parameters);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        #endregion

        /// <summary>
        /// GetSpStatement
        /// </summary>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="procedureName"></param>
        /// <param name="parameters"></param>
        /// <param name="extendedParameters"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetSpStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String procedureName, StatementParameterCollection parameters, IDictionary extendedParameters, OperationType? operationType = null)
        {
            return GetDefaultSpStatement(table, logicDbName, shardingStrategy, procedureName, parameters, extendedParameters, SqlStatementType.SELECT, operationType ?? OperationType.Default);
        }

        /// <summary>
        /// GetSpNonQueryStatement
        /// </summary>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="procedureName"></param>
        /// <param name="parameters"></param>
        /// <param name="extendedParameters"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetSpNonQueryStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String procedureName, StatementParameterCollection parameters, IDictionary extendedParameters, OperationType? operationType = null)
        {
            return GetDefaultSpStatement(table, logicDbName, shardingStrategy, procedureName, parameters, extendedParameters, SqlStatementType.UNKNOWN, operationType ?? OperationType.Write);
        }

        private static Statement GetDefaultSpStatement(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String procedureName, StatementParameterCollection parameters, IDictionary hints, SqlStatementType sqlType, OperationType? operationType = null)
        {
            if (String.IsNullOrEmpty(logicDbName)) throw new DalException("Please specify databaseSet.");
            if (String.IsNullOrEmpty(procedureName)) throw new DalException("Please specify stored procedure.");

            //自动识别Shard字段
            var tuple = ShardingUtil.GetShardInfo<Object>(logicDbName, shardingStrategy, null, table.ColumnList, parameters, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.StoredProcedure, operationType ?? OperationType.Default, sqlType, hints, tuple.Item1);
            statement.StatementText = procedureName;
            statement.Parameters = parameters;
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        /// <summary>
        /// GetPartialSpNonQueryStatement
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="table"></param>
        /// <param name="logicDbName"></param>
        /// <param name="shardingStrategy"></param>
        /// <param name="procedureName"></param>
        /// <param name="item"></param>
        /// <param name="updatePartial"></param>
        /// <param name="hints"></param>
        /// <param name="operationType"></param>
        /// <returns></returns>
        public static Statement GetPartialSpNonQueryStatement<T>(SqlTable table, String logicDbName, IShardingStrategy shardingStrategy,
            String procedureName, T item, IUpdatePartial<T> updatePartial, IDictionary hints, OperationType? operationType = null) where T : class, new()
        {
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");
            if (String.IsNullOrEmpty(procedureName))
                throw new DalException("Please specify stored procedure.");
            if (item == null)
                throw new DalException("Entity can't be null.");

            var partially = updatePartial as UpdatePartial<T>;
            if (partially == null)
                throw new DalException("IUpdatePartial can't be null.");

            var setFields = partially.SetFields;
            var whereFields = partially.WhereConditions;
            if (setFields == null || whereFields == null)
                throw new DalException("Expression of IUpdatePartial can't be null.");

            ISet<String> sets = new HashSet<String>();
            ISet<String> wheres = new HashSet<String>();

            foreach (var set in setFields)
            {
                sets.Add(set.ToUpper());
            }
            foreach (var where in whereFields)
            {
                wheres.Add(where.ToUpper());
            }

            foreach (var where in wheres)
            {
                if (sets.Contains(where))
                    throw new ArgumentException(String.Format("Execute sp partailly failed, field {0} duplicated on set condition and where condition.", where));
            }

            var tuple = ShardingUtil.GetShardInfo(logicDbName, shardingStrategy, item, table.ColumnList, null, hints);
            Statement statement = GetStatement(logicDbName, Arch.Data.Common.Enums.StatementType.StoredProcedure, operationType ?? OperationType.Write, SqlStatementType.UNKNOWN, hints, tuple.Item1);
            statement.StatementText = procedureName;
            IDbDialect dbDialect = DbDialectFactory.Build(statement.ProviderType);
            IList<IColumn> pkColumns;
            IDictionary<String, IColumn> columnDict = FilterColumnsDict(table.Columns, dbDialect, item, true, false, false, false, false, true, out pkColumns);
            var nullValueColumns = columnDict.Where(p => !sets.Contains(p.Key)).Where(p => !wheres.Contains(p.Key));

            foreach (var column in nullValueColumns)
            {
                StatementParameter parameter = new StatementParameter
                {
                    DbType = SqlUtils.GetDbType(column.Value),
                    Direction = ParameterDirection.Input,
                    Name = dbDialect.QuoteParameter(column.Value.Name),
                    Value = null,
                    Size = column.Value.Length
                };

                statement.Parameters.Add(parameter);
            }


            var setColumns = columnDict.Where(p => sets.Contains(p.Key)).ToDictionary(p => p.Key, p => p.Value);
            BuildSpParameters(statement.Parameters, setColumns.Values, item, dbDialect);

            var whereColumns = columnDict.Where(p => wheres.Contains(p.Key)).ToDictionary(p => p.Key, p => p.Value);
            BuildSpParameters(statement.Parameters, whereColumns.Values, item, dbDialect);
            CurrentStackCustomizedLog(statement);
            return statement;
        }

        private static void CurrentStackCustomizedLog(Statement statement)
        {
            StackTrace stackTrace = new StackTrace(false);
            StringBuilder sb = new StringBuilder();

            for (Int32 x = 2; x < stackTrace.FrameCount; ++x)
            {
                var stackFrame = stackTrace.GetFrame(x);

                if (IsMethodToBeIncluded(stackFrame))
                {
                    var method = stackFrame.GetMethod();
                    if (method.ReflectedType == null) continue;
                    statement.Invoker = method.ReflectedType.FullName;
                    sb.Append(method.Name).Append("(");
                    var parameters = method.GetParameters();

                    if (parameters.Length > 0)
                    {
                        foreach (var parameter in parameters)
                        {
                            sb.Append(parameter.ParameterType.Name).Append(",");
                        }

                        sb.Remove(sb.Length - 1, 1);
                    }

                    sb.Append(")");
                    statement.InvokeMethod = sb.ToString();
                    break;
                }
            }
        }

        private static Boolean IsMethodToBeIncluded(StackFrame stackFrame)
        {
            var method = stackFrame.GetMethod();
            return method.DeclaringType == null || method.DeclaringType.FullName == null || !method.DeclaringType.FullName.Contains(BaseDaoName);
        }

        private const String BaseDaoName = "Arch.Data";

    }
}
