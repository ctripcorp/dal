using Arch.Data.Common.Util;
using Arch.Data.DbEngine.DB;
using Arch.Data.Orm.sql;
using System;
using System.Collections.Generic;
using System.Data;

namespace Arch.Data.DbEngine.Sharding
{
    class ShardingExecutor
    {
        #region ExecuteShardTable
        /// <summary>
        /// 表结构均相同, 只有一个数据库，将一个表分为多个名称不同的表
        /// </summary>
        /// <param name="statements"></param>
        /// <returns></returns>
        public static DataTable ExecuteShardTable(IList<Statement> statements)
        {
            if (statements == null || statements.Count == 0)
                return null;

            var excuter = new List<Func<IDataReader>>();
            for (Int32 i = 0; i < statements.Count; i++)
            {
                Statement statement = statements[i];
                excuter.Add(() =>
                {
                    try
                    {
                        LogManager.Logger.StartTracing();
                        return DatabaseBridge.Instance.ExecuteReader(statement);
                    }
                    finally
                    {
                        LogManager.Logger.StopTracing();
                    }
                });
            }

            var result = ExecuteParallelHelper.ParallelExcuter(excuter, CheckSameShard(statements));
            return MergeDataReader(result);
        }

        public static IList<T> ExecuteShardingList<T>(IList<Statement> statements, SqlTable sqlTable)
        {
            if (statements == null || statements.Count == 0)
                return null;

            var dataSets = GetShardingDataSetList(statements);
            return MergeList<T>(dataSets, sqlTable);
        }

        public static IList<T> ExecuteShardingFirst<T>(IList<Statement> statements, SqlTable sqlTable)
        {
            if (statements == null || statements.Count == 0)
                return null;

            var dataReaders = GetShardingDataReaderList(statements);
            return MergeFirst<T>(dataReaders, sqlTable, CheckSameShard(statements));
        }

        public static IList<T> ExecuteShardingListOfSingleField<T>(IList<Statement> statements)
        {
            if (statements == null || statements.Count == 0)
                return null;

            var dataReaders = GetShardingDataReaderList(statements);
            return MergeListOfSingleField<T>(dataReaders, CheckSameShard(statements));
        }

        public static List<int> ExecuteShardingNonQuery(IList<Statement> statements)
        {
            if (statements == null || statements.Count == 0)
                return null;

            var funcs = new List<Func<Int32>>();
            for (Int32 i = 0; i < statements.Count; i++)
            {
                Statement statement = statements[i];
                funcs.Add(() =>
                {
                    try
                    {
                        LogManager.Logger.StartTracing();
                        return DatabaseBridge.Instance.ExecuteNonQuery(statement);
                    }
                    finally
                    {
                        LogManager.Logger.StopTracing();
                    }
                });
            }

            return ExecuteParallelHelper.ParallelExcuter(funcs, CheckSameShard(statements));
        }

        public static IList<object> ExecuteShardingScalar(IList<Statement> statements)
        {
            if (statements == null || statements.Count == 0)
                return null;

            var funcs = new List<Func<object>>();
            for (Int32 i = 0; i < statements.Count; i++)
            {
                Statement statement = statements[i];
                funcs.Add(() =>
                {
                    try
                    {
                        LogManager.Logger.StartTracing();
                        return DatabaseBridge.Instance.ExecuteScalar(statement);
                    }
                    finally
                    {
                        LogManager.Logger.StopTracing();
                    }
                });
            }

            return ExecuteParallelHelper.ParallelExcuter(funcs, CheckSameShard(statements));
        }

        public static DataSet ExecuteShardingDataSet(IList<Statement> statements)
        {
            if (statements == null || statements.Count == 0)
                return null;

            var dataSets = GetShardingDataSetList(statements);
            return MergeDataSet(dataSets);
        }

        private static IList<DataSet> GetShardingDataSetList(IList<Statement> statements)
        {
            var dataSets = new List<Func<DataSet>>();

            for (Int32 i = 0; i < statements.Count; i++)
            {
                Statement statement = statements[i];
                dataSets.Add(() =>
                {
                    try
                    {
                        LogManager.Logger.StartTracing();
                        return DatabaseBridge.Instance.ExecuteDataSet(statement);
                    }
                    finally
                    {
                        LogManager.Logger.StopTracing();
                    }
                });
            }

            return ExecuteParallelHelper.ParallelExcuter(dataSets, CheckSameShard(statements));
        }

        public static IList<IDataReader> GetShardingDataReaderList(IList<Statement> statements)
        {
            var dataReaders = new List<Func<IDataReader>>();

            for (Int32 i = 0; i < statements.Count; i++)
            {
                Statement statement = statements[i];
                dataReaders.Add(() =>
                {
                    try
                    {
                        LogManager.Logger.StartTracing();
                        return DatabaseBridge.Instance.ExecuteReader(statement);
                    }
                    finally
                    {
                        LogManager.Logger.StopTracing();
                    }
                });
            }

            return ExecuteParallelHelper.ParallelExcuter(dataReaders, CheckSameShard(statements));
        }

        #endregion

        private static DataTable MergeDataReader(IList<IDataReader> dataReaders)
        {
            if (dataReaders == null || dataReaders.Count == 0)
                return null;

            var dataTable = new DataTable();

            foreach (var dataReader in dataReaders)
            {
                var dt = new DataTable();
                dt.Load(dataReader);
                dataTable.Merge(dt);
            }

            return dataTable;
        }

        private static IList<T> MergeList<T>(IList<DataSet> dataSets, SqlTable sqlTable)
        {
            if (dataSets == null || dataSets.Count == 0)
                return null;
            if (sqlTable == null || sqlTable.ColumnList == null || sqlTable.ColumnList.Count == 0)
                return null;

            var result = new List<T>();

            foreach (var dataSet in dataSets)
            {
                if (dataSet != null && dataSet.Tables.Count > 0)
                {
                    var dataTable = dataSet.Tables[0];
                    var list = new List<T>();
                    OrmUtil.FillDataTableByName(dataTable, sqlTable.ColumnList, typeof(T), list);
                    result.AddRange(list);
                }
            }

            return result;
        }

        private static IList<T> MergeFirst<T>(IList<IDataReader> dataReaders, SqlTable sqlTable, Boolean isSameShard)
        {
            if (dataReaders == null || dataReaders.Count == 0)
                return null;

            if (sqlTable == null || sqlTable.ColumnList == null || sqlTable.ColumnList.Count == 0)
                return null;

            var funcs = new List<Func<T>>();

            for (Int32 i = 0; i < dataReaders.Count; i++)
            {
                var dataReader = dataReaders[i];
                funcs.Add(() =>
                {
                    try
                    {
                        LogManager.Logger.StartTracing();
                        T item = default(T);
                        using (dataReader)
                        {
                            OrmUtil.FillFirstByName(dataReader, sqlTable.ColumnList, ref item);
                        }
                        return item;
                    }
                    finally
                    {
                        LogManager.Logger.StopTracing();
                    }
                });
            }

            var result = ExecuteParallelHelper.ParallelExcuter(funcs, isSameShard);
            return result;
        }

        private static IList<T> MergeListOfSingleField<T>(IList<IDataReader> dataReaders, Boolean isSameShard)
        {
            if (dataReaders == null || dataReaders.Count == 0)
                return null;

            var result = new List<T>();
            var funcs = new List<Func<IList<T>>>();

            for (Int32 i = 0; i < dataReaders.Count; i++)
            {
                var dataReader = dataReaders[i];
                funcs.Add(() =>
                {
                    try
                    {
                        LogManager.Logger.StartTracing();
                        var list = new List<T>();
                        using (dataReader)
                        {
                            OrmUtil.FillBySingleFied(dataReader, list);
                        }
                        return list;
                    }
                    finally
                    {
                        LogManager.Logger.StopTracing();
                    }
                });
            }

            var temp = ExecuteParallelHelper.ParallelExcuter(funcs, isSameShard);

            if (temp.Count > 0)
            {
                foreach (var item in temp)
                {
                    result.AddRange(item);
                }
            }

            return result;
        }

        private static DataSet MergeDataSet(IList<DataSet> dataSets)
        {
            if (dataSets == null || dataSets.Count == 0)
                return null;

            DataSet result = null;
            foreach (var dataSet in dataSets)
            {
                if (dataSet != null)
                {
                    if (result == null)
                    {
                        result = dataSet;
                    }
                    else
                    {
                        result.Merge(dataSet);
                    }
                }
            }

            return result;
        }

        private static bool CheckSameShard(IList<Statement> statements)
        {
            var shardId = (string)null;
            var first = true;
            foreach (var item in statements)
            {
                if (first)
                {
                    first = false;
                    shardId = item.ShardID;
                    continue;
                }
                if (item.ShardID != shardId)
                {
                    return false;
                }
            }
            return true;
        }

    }
}
