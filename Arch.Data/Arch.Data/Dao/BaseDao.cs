using Arch.Data.Common.constant;
using Arch.Data.Common.Enums;
using Arch.Data.Common.Util;
using Arch.Data.DbEngine;
using Arch.Data.DbEngine.DB;
using Arch.Data.DbEngine.Sharding;
using Arch.Data.Orm;
using Arch.Data.Orm.partially;
using Arch.Data.Orm.sql;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using System.Linq;

namespace Arch.Data
{
    /// <summary>
    /// 数据库访问类，一般通过BaseDaoFactory的静态方法CreateBaseDao创建
    /// </summary>
    public class BaseDao
    {
        /// <summary>
        /// DatabaseSet name
        /// </summary>
        private readonly String LogicDbName;

        private readonly SqlTable Table = SqlTableFactory.Instance.Build();

        private readonly Lazy<IShardingStrategy> shardingStrategyLazy;

        /// <summary>
        ///当前的分片策略， 使用ShardingStrategy时，需要确保已经指定了逻辑数据库名，即 new BaseDao("逻辑数据库名");
        /// </summary>
        public IShardingStrategy ShardingStrategy { get { return shardingStrategyLazy.Value; } }

        private Boolean IsShardEnabled
        {
            get { return ShardingStrategy != null; }
        }

        /// <summary>
        /// 构造初始化
        /// </summary>
        /// <param name="logicDbName">逻辑数据库名</param>
        public BaseDao(String logicDbName)
        {
            if (String.IsNullOrEmpty(logicDbName))
                throw new DalException("Please specify databaseSet.");

            LogicDbName = logicDbName;
            shardingStrategyLazy = new Lazy<IShardingStrategy>(() => DALBootstrap.GetShardingStrategy(logicDbName), true);
        }

        /// <summary>
        /// 获得查询对象
        /// </summary>
        /// <returns></returns>
        [Obsolete("此方法不推荐使用，请用GetQuery<T>()来代替")]
        public IQuery GetQuery()
        {
            return new SqlQuery();
        }

        /// <summary>
        /// 获得查询对象
        /// </summary>
        /// <typeparam name="T">查询实体类</typeparam>
        /// <returns>查询对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IQuery<T> GetQuery<T>() where T : class, new()
        {
            SqlTable table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));
            return new SqlQuery<T>(table);
        }

        /// <summary>
        /// 获取部分更新对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <returns></returns>
        public IUpdatePartial<T> GetUpdatePartially<T>() where T : class, new()
        {
            return new UpdatePartial<T>();
        }

        #region DataTable,IDataReader to List

        /// <summary>
        /// 将DataTable填充到IList中去
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="dataTable"></param>
        /// <returns></returns>
        public IList<T> GetListFromDataTable<T>(DataTable dataTable) where T : class, new()
        {
            SqlTable table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));
            IList<T> list = new List<T>();
            OrmUtil.FillDataTableByName(dataTable, table.ColumnList, typeof(T), list);
            return list;
        }

        /// <summary>
        /// 将IDataReader填充到IList中去，请手动关闭IDataReader
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="reader"></param>
        /// <returns></returns>
        public IList<T> GetListFromIDataReader<T>(IDataReader reader) where T : class, new()
        {
            SqlTable table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));
            IList<T> list = new List<T>();
            OrmUtil.FillByName(reader, table.ColumnList, list);
            return list;
        }

        #endregion

        #region Insert

        /// <summary>
        /// 插入对象 
        /// <example><para>示例：</para>
        /// <code>
        /// readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("your databaseSetName");
        ///  City c = new City
        ///                 {
        ///                     Name = "test",
        ///                     Population = "test",
        ///                     Country1Code = "test",
        ///                     District = "test"
        ///                 };
        /// baseDao.Insert(city);
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.实体类打上了[ID]这个标签，插入成功将返回新增的主键值</para> 
        /// <para> 2.实体类打上了[PK]这个标签，(插入成功将返回新增的主键值，如果PK不为ID，则返回影响条数)</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="obj">实体对象</param>
        /// <returns>返回第一列值（譬如自增长ID）</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object Insert<T>(T obj) where T : class, new()
        {
            return Insert(obj, null);
        }

        /// <summary>
        /// 插入对象，需要有增删改的权限
        /// <example><para>示例：</para>
        /// <code>
        ///  readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("your databaseSetName");<br/>
        ///  City c = new City
        ///                 {
        ///                     Name = "test",
        ///                     Population = "test",
        ///                     Country1Code = "test",
        ///                     District = "test"
        ///                 };
        ///  IDictionary statementParams = new  new Hashtable();
        ///  statementParams.Add(DALExtStatementConstant.TIMEOUT, "1000");
        ///  baseDao.Insert("yourdatabaseSetName",city,statementParams);
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.实体类打上了[ID]这个标签，插入成功将返回新增的主键值</para> 
        /// <para> 2.实体类打上了[PK]这个标签，(插入成功将返回新增的主键值，如果PK不为ID，则返回影响条数)</para> 
        /// <para> 3.扩展属性的key，具体可以查询DALExtStatementConstant的常量列表（譬如：timeout等）</para> 
        /// <para> 4.可以通过指定的逻辑数据库名插入数据，也就是一个dao(数据访问对象)支持多个数据库连接</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="obj">实体对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout） </param>
        /// <returns>返回第一列值（譬如自增长ID）</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object Insert<T>(T obj, IDictionary hints) where T : class, new()
        {
            return Insert(obj, hints, false);
        }

        #endregion

        #region InsertByComplexPk

        /// <summary>
        ///  插入对象(复合主键)
        /// <example><para>示例：</para>
        /// <code>
        ///  readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("your databaseSetName");<br/>
        ///  City c = new City
        ///                 {
        ///                     Name = "test",
        ///                     Population = "test",
        ///                     Country1Code = "test",
        ///                     District = "test"
        ///                 };
        ///  IDictionary statementParams = new  new Hashtable();
        ///  statementParams.Add(DALExtStatementConstant.TIMEOUT, "1000");
        ///  baseDao.InsertByComplexPk("yourdatabaseSetName",city,statementParams);
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.实体类打上了多个[PK]这个标签，插入成功将返回影响行数</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="obj">实体对象</param>
        /// <returns>返回影响行数</returns>
        public Object InsertByComplexPk<T>(T obj) where T : class, new()
        {
            return InsertByComplexPk(obj, null);
        }

        /// <summary>
        /// 插入对象(复合主键)
        /// <example><para>示例：</para>
        /// <code>
        ///  readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("your databaseSetName");<br/>
        ///  City c = new City
        ///                 {
        ///                     Name = "test",
        ///                     Population = "test",
        ///                     Country1Code = "test",
        ///                     District = "test"
        ///                 };
        ///  IDictionary statementParams = new  new Hashtable();
        ///  statementParams.Add(DALExtStatementConstant.TIMEOUT, "1000");
        ///  baseDao.InsertByComplexPk("yourdatabaseSetName",city,statementParams);
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.实体类打上了多个[PK]这个标签，插入成功将返回影响行数</para> 
        /// <para> 2.扩展属性的key，具体可以查询DALExtStatementConstant的常量列表（譬如：timeout等）</para> 
        /// <para> 3.可以通过指定的逻辑数据库名插入数据，也就是一个dao(数据访问对象)支持多个数据库连接</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="obj">实体对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout）</param>
        /// <returns>返回影响行数</returns>

        private Object InsertByComplexPk<T>(T obj, IDictionary hints) where T : class, new()
        {
            return Insert(obj, hints, true);
        }

        #endregion

        private Object Insert<T>(T obj, IDictionary hints, Boolean isCompositeKey) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                Object result = null;
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetInsertSqlStatement(table, obj, LogicDbName, ShardingStrategy, hints, isCompositeKey);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    if (isCompositeKey)
                    {
                        result = DatabaseBridge.Instance.ExecuteNonQuery(statement);
                    }
                    else
                    {
                        result = table.Identity == null ? DatabaseBridge.Instance.ExecuteNonQuery(statement) : DatabaseBridge.Instance.ExecuteScalar(statement);
                    }
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatementByEntity(LogicDbName, ShardingStrategy, new List<T> { obj }, table, hints,
                        (newList, newHints) => SqlBuilder.GetInsertSqlStatement(table, obj, LogicDbName, ShardingStrategy, newHints, isCompositeKey));

                    if (isCompositeKey)
                    {
                        result = ShardingExecutor.ExecuteShardingNonQuery(statements).Sum();
                    }
                    else
                    {
                        if (table.Identity == null)
                        {
                            result = ShardingExecutor.ExecuteShardingNonQuery(statements).Sum();
                        }
                        else
                        {
                            var temp = ShardingExecutor.ExecuteShardingScalar(statements);
                            if (temp.Count > 0) result = temp[0];
                        }
                    }
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #region BulkInsert

        /// <summary>
        /// 批量插入对象
        /// <example><para>示例：</para>
        /// <code>
        /// <para>
        ///   readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("your databaseSetName");
        ///   List( &lt; UserPrizeDrawedResultsEntity  &gt; list = new List &lt; UserPrizeDrawedResultsEntity &gt;();
        ///   UserPrizeDrawedResultsEntity item = null;
        ///   for (int i = 0; i  &lt; 100; i++)
        ///   {
        ///        item = new UserPrizeDrawedResultsEntity();
        ///        item.PrizeDrawTime ="test";
        ///        item.PrizeInfoID = "test";
        ///        item.PrizeActivityId = "test";
        ///        item.Uid = "test";
        ///        list.Add(item);
        ///    }
        ///   baseDao.BulkInsert(list);</para>
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.batchSize最大10000，建议100，分批插入。</para> 
        /// <para> 2.该批量插入推荐在mysql中使用，如果MsSQL推荐用表变量方式处理</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="list">对象集合</param>
        /// <returns>是否成功</returns>
        /// <exception cref="DalException">数据访问异常</exception>
        public Boolean BulkInsert<T>(IList<T> list) where T : class, new()
        {
            return BulkInsert(list, null);
        }

        /// <summary>
        /// 批量插入对象,batchSize：100
        /// <example><para>示例：</para>
        /// <code>
        /// <para>
        ///   readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("your databaseSetName");
        ///   List( &lt; UserPrizeDrawedResultsEntity  &gt; list = new List &lt; UserPrizeDrawedResultsEntity &gt;();
        ///   IDictionary statementParams = new  new Hashtable();
        ///   statementParams.Add(DALExtStatementConstant.TIMEOUT, "1000");
        ///   UserPrizeDrawedResultsEntity item = null;
        ///   for (int i = 0; i  &lt; 100; i++)
        ///   {
        ///        item = new UserPrizeDrawedResultsEntity();
        ///        item.PrizeDrawTime ="test";
        ///        item.PrizeInfoID = "test";
        ///        item.PrizeActivityId = "test";
        ///        item.Uid = "test";
        ///        list.Add(item);
        ///    }
        ///   baseDao.BulkInsert(list);</para>
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:批量插入对象</para>
        /// <para> 1.batchSize最大10000，建议100，分批插入。</para> 
        /// <para> 2.该批量插入推荐在mysql中使用，如果MsSQL推荐用表变量方式处理</para> 
        /// <para> 3.扩展属性的key，具体可以查询DALExtStatementConstant的常量列表（譬如：timeout等）</para>
        /// <para> 4.可以通过指定的逻辑数据库名插入数据，也就是一个dao(数据访问对象)支持多个数据库连接</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="list">对象集合</param>
        /// <param name="hints">扩展属性，譬如:timeout等 </param>
        /// <returns>是否成功</returns>
        /// <remarks>1.集合对象不能超过10000, 2.SQLServer参数不能超过2100, Mysql控制报文最大长度</remarks>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Boolean BulkInsert<T>(IList<T> list, IDictionary hints) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();

                if (list != null && (list.Count > 10000 || list.Count < 1))
                    throw new DalException("DAL仅支持1到10000条批量插入，请在调用前做筛选！");

                Boolean result;
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetBulkInsertSqlStatement(table, list, LogicDbName, ShardingStrategy, hints);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    result = DatabaseBridge.Instance.ExecuteNonQuery(statement) > 0;
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatementByEntity(LogicDbName, ShardingStrategy, list, table, hints,
                        (newList, newHints) => SqlBuilder.GetBulkInsertSqlStatement(table, newList, LogicDbName, ShardingStrategy, newHints));

                    result = ShardingExecutor.ExecuteShardingNonQuery(statements).TrueForAll(p => p > 0);
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region Update

        /// <summary>
        /// 更新记录
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="obj">实体对象</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 Update<T>(T obj) where T : class, new()
        {
            return Update(obj, null);
        }

        /// <summary>
        /// 更新记录
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="obj">对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout）</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 Update<T>(T obj, IDictionary hints) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                Int32 result;
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetUpdateSqlStatement(table, obj, LogicDbName, ShardingStrategy, hints);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    result = DatabaseBridge.Instance.ExecuteNonQuery(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatementByEntity(LogicDbName, ShardingStrategy, new List<T> { obj }, table, hints,
                        (newList, newHints) => SqlBuilder.GetUpdateSqlStatement(table, obj, LogicDbName, ShardingStrategy, newHints));

                    result = ShardingExecutor.ExecuteShardingNonQuery(statements).Sum();
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region UpdatePartially

        /// <summary>
        /// 更新记录
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="partially"></param>
        /// <param name="obj">实体对象</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 UpdatePartially<T>(IUpdatePartial<T> partially, T obj) where T : class, new()
        {
            return UpdatePartially(partially, obj, null);
        }

        /// <summary>
        /// 更新记录
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="partially"></param>
        /// <param name="obj">对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout）</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 UpdatePartially<T>(IUpdatePartial<T> partially, T obj, IDictionary hints) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                partially.Validate();
                Int32 result;
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetUpdatePartialSqlStatement(table, obj, LogicDbName, ShardingStrategy, partially, hints);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    result = DatabaseBridge.Instance.ExecuteNonQuery(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatementByEntity(LogicDbName, ShardingStrategy, new List<T> { obj }, table, hints,
                        (newList, newHints) => SqlBuilder.GetUpdatePartialSqlStatement(table, obj, LogicDbName, ShardingStrategy, partially, newHints));

                    result = ShardingExecutor.ExecuteShardingNonQuery(statements).Sum();
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        /// <summary>
        /// 更新记录，调用此方法有如下要求：存储过程参数名必须与对应的表字段名一致
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="partially"></param>
        /// <param name="spName"></param>
        /// <param name="obj">实体对象</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 ExecSpPartially<T>(IUpdatePartial<T> partially, String spName, T obj) where T : class, new()
        {
            return ExecSpPartially(partially, spName, obj, null);
        }

        /// <summary>
        /// 更新记录，调用此方法有如下要求：存储过程参数名必须与对应的表字段名一致
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="partially"></param>
        /// <param name="spName"></param>
        /// <param name="obj">对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout）</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 ExecSpPartially<T>(IUpdatePartial<T> partially, String spName, T obj, IDictionary hints) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                partially.Validate();
                Object temp = null;
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetPartialSpNonQueryStatement(table, LogicDbName, ShardingStrategy, spName, obj, partially, hints, OperationType.Write);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    temp = DatabaseBridge.Instance.ExecuteScalar(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatementByEntity(LogicDbName, ShardingStrategy, new List<T> { obj }, table, hints,
                        (newList, newHints) => SqlBuilder.GetPartialSpNonQueryStatement(table, LogicDbName, ShardingStrategy, spName, obj, partially, newHints, OperationType.Write));

                    var list = ShardingExecutor.ExecuteShardingScalar(statements);
                    if (list.Count > 0) temp = list[0];
                }

                Int32 result;

                try
                {
                    result = temp == null ? -1 : Convert.ToInt32(temp);
                }
                catch
                {
                    result = -1;
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region Delete

        /// <summary>
        /// 删除记录
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="obj">对象</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 Delete<T>(T obj) where T : class, new()
        {
            return Delete(obj, null);
        }

        /// <summary>
        /// 删除记录
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="obj">对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout）</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 Delete<T>(T obj, IDictionary hints) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                Int32 result;
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetDeleteSqlStatement(table, obj, LogicDbName, ShardingStrategy, hints);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    result = DatabaseBridge.Instance.ExecuteNonQuery(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatementByEntity(LogicDbName, ShardingStrategy, new List<T> { obj }, table, hints,
                        (newList, newHints) => SqlBuilder.GetDeleteSqlStatement(table, obj, LogicDbName, ShardingStrategy, newHints));
                    result = ShardingExecutor.ExecuteShardingNonQuery(statements).Sum();
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region ORM Get and Select

        #region SelectListOfSingleField

        /// <summary>
        /// 取一个字段的结果集，返回该字段的List
        /// </summary>
        /// <typeparam name="T">字段类型</typeparam>
        /// <param name="sql">SQL语句</param>
        /// <returns>该字段的结果集</returns>
        public IList<T> SelectListOfSingleField<T>(String sql)
        {
            return SelectListOfSingleField<T>(sql, null);
        }

        /// <summary>
        /// 取一个字段的结果集，返回该字段的List
        /// </summary>
        /// <typeparam name="T">字段类型</typeparam>
        /// <param name="sql">SQL语句</param>
        /// <param name="parameters">参数</param>
        /// <returns>该字段的结果集</returns>
        public IList<T> SelectListOfSingleField<T>(String sql, StatementParameterCollection parameters)
        {
            return SelectListOfSingleField<T>(sql, parameters, null);
        }

        /// <summary>
        /// 取一个字段的结果集，返回该字段的List
        /// </summary>
        /// <typeparam name="T">字段类型</typeparam>
        /// <param name="sql">SQL语句</param>
        /// <param name="parameters">参数</param>
        /// <param name="hints">扩展参数，如timeout, shardid等</param>
        /// <returns>该字段的结果集</returns>
        public IList<T> SelectListOfSingleField<T>(String sql, StatementParameterCollection parameters, IDictionary hints)
        {
            return SelectListOfSingleField<T>(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 取一个字段的结果集，返回该字段的List
        /// </summary>
        /// <typeparam name="T">字段类型</typeparam>
        /// <param name="sql">SQL语句</param>
        /// <param name="parameters">参数</param>
        /// <param name="hints">扩展参数，如timeout, shardid等</param>
        /// <param name="operationType">读写分离类型，Read强制到Slave库，Write强制到Master库</param>
        /// <returns>该字段的结果集</returns>
        public IList<T> SelectListOfSingleField<T>(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSqlStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillBySingleFied(reader, list);
                    }
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetSqlStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, newHints));

                    list = ShardingExecutor.ExecuteShardingListOfSingleField<T>(statements);
                }

                return list;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region GetByKey

        /// <summary>
        /// 根据主键获得一个对象,输入值类型一定要和实体类生成的主键类型一致，不支持联合组键
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="key">单一主键</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public T GetByKey<T>(Object key) where T : class, new()
        {
            return GetByKey<T>(key, null);
        }

        /// <summary>
        ///  根据主键获得一个对象,输入值类型一定要和实体类生成的主键类型一致，不支持联合组键
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="key">单一主键</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public T GetByKey<T>(Object key, IDictionary hints) where T : class, new()
        {
            return GetByKey<T>(key, hints, OperationType.Default);
        }


        /// <summary>
        ///  根据主键获得一个对象,输入值类型一定要和实体类生成的主键类型一致，不支持联合组键
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="key">单一主键</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从Master库读取</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public T GetByKey<T>(Object key, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();
                T result = default(T);
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetFindByKeyStatement(table, LogicDbName, ShardingStrategy, hints, key, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillByName(reader, table.ColumnList, list);
                    }
                }
                else
                {
                    if (hints == null)
                        hints = new Dictionary<String, String>();

                    if (!hints.Contains(DALExtStatementConstant.SHARDID) && !hints.Contains(DALExtStatementConstant.TABLEID))
                    {
                        //need verify the key is shard column first
                        if (!ShardingUtil.CheckKeyIsShardColumn(ShardingStrategy, table))
                            throw new DalException("Please provide shard hints for GetByKey<T> method.");

                        String sid = ShardingStrategy.ComputeShardId(key as IComparable);
                        if (ShardingStrategy.ShardByDB)
                        {
                            hints[DALExtStatementConstant.SHARDID] = sid;
                        }
                        else if (ShardingStrategy.ShardByTable)
                        {
                            hints[DALExtStatementConstant.TABLEID] = sid;
                        }
                    }

                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, null, hints,
                        newHints => SqlBuilder.GetFindByKeyStatement(table, LogicDbName, ShardingStrategy, newHints, key, operationType));
                    list = ShardingExecutor.ExecuteShardingList<T>(statements, table);
                }

                if (list.Count > 0) result = list[0];
                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region GetAll

        /// <summary>
        /// 获取所有记录
        /// </summary>
        /// <typeparam name="T">实体类</typeparam>
        /// <returns>所有对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("谨慎使用此方法，容易占用太多内存！")]
        public IList<T> GetAll<T>() where T : class, new()
        {
            return GetAll<T>(OperationType.Default);
        }

        /// <summary>
        /// 获取所有记录
        /// </summary>
        /// <typeparam name="T">实体类</typeparam>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>所有对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("谨慎使用此方法，容易占用太多内存！")]
        public IList<T> GetAll<T>(OperationType operationType) where T : class, new()
        {
            return GetAll<T>(null, operationType);
        }

        /// <summary>
        /// 获取所有记录
        /// </summary>
        /// <typeparam name="T">实体类</typeparam>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>所有对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("谨慎使用此方法，容易占用太多内存！")]
        public IList<T> GetAll<T>(IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetAllStatement(table, LogicDbName, ShardingStrategy, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillByName(reader, table.ColumnList, list);
                    }
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, null, hints,
                        newHints => SqlBuilder.GetAllStatement(table, LogicDbName, ShardingStrategy, newHints));

                    list = ShardingExecutor.ExecuteShardingList<T>(statements, table);
                }

                return list;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region SelectList Sql

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectList<T>(String sql) where T : class, new()
        {
            return SelectList<T>(sql, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectList<T>(String sql, StatementParameterCollection parameters) where T : class, new()
        {
            return SelectList<T>(sql, parameters, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectList<T>(String sql, StatementParameterCollection parameters, IDictionary hints) where T : class, new()
        {
            return SelectList<T>(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectList<T>(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSqlStatement(table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillByName(reader, table.ColumnList, list);
                    }
                }
                else
                {
                    list = SelectList<T>(sql, parameters, hints, operationType, table);
                }

                return list;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectListByAdapter<T>(String sql) where T : class, new()
        {
            return SelectListByAdapter<T>(sql, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectListByAdapter<T>(String sql, StatementParameterCollection parameters) where T : class, new()
        {
            return SelectListByAdapter<T>(sql, parameters, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectListByAdapter<T>(String sql, StatementParameterCollection parameters, IDictionary hints) where T : class, new()
        {
            return SelectListByAdapter<T>(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>结果集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectListByAdapter<T>(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSqlStatement(table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var dataSet = DatabaseBridge.Instance.ExecuteDataSet(statement, null))
                    {
                        if (dataSet != null && dataSet.Tables.Count > 0)
                        {
                            var dataTable = dataSet.Tables[0];
                            OrmUtil.FillDataTableByName(dataTable, table.ColumnList, table.Class, list);
                        }
                    }
                }
                else
                {
                    list = SelectList<T>(sql, parameters, hints, operationType, table);
                }

                return list;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        private IList<T> SelectList<T>(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType, SqlTable table) where T : class, new()
        {
            var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                newHints => SqlBuilder.GetSqlStatement(table, LogicDbName, ShardingStrategy, sql, parameters, newHints, operationType));
            return ShardingExecutor.ExecuteShardingList<T>(statements, table);
        }

        #region SelectList IQuery

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectList<T>(IQuery query) where T : class, new()
        {
            return SelectList<T>(query, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectList<T>(IQuery query, IDictionary hints) where T : class, new()
        {
            return SelectList<T>(query, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectList<T>(IQuery query, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetQueryStatement(table, LogicDbName, ShardingStrategy, query, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillByName(reader, table.ColumnList, list);
                    }
                }
                else
                {
                    list = SelectList<T>(query, hints, table);
                }

                return list;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectListByAdapter<T>(IQuery query) where T : class, new()
        {
            return SelectListByAdapter<T>(query, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectListByAdapter<T>(IQuery query, IDictionary hints) where T : class, new()
        {
            return SelectListByAdapter<T>(query, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> SelectListByAdapter<T>(IQuery query, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();

                SqlTable table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetQueryStatement(table, LogicDbName, ShardingStrategy, query, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (DataSet ds = DatabaseBridge.Instance.ExecuteDataSet(statement, null))
                    {
                        if (ds != null && ds.Tables.Count > 0)
                        {
                            DataTable currentTable = ds.Tables[0];
                            OrmUtil.FillDataTableByName(currentTable, table.ColumnList, table.Class, list);
                        }
                    }
                }
                else
                {
                    return SelectList<T>(query, hints, table);
                }

                return list;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        private IList<T> SelectList<T>(IQuery query, IDictionary hints, SqlTable table) where T : class, new()
        {
            var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, null, hints,
                newHints => SqlBuilder.GetQueryStatement(table, LogicDbName, ShardingStrategy, query, newHints));
            return ShardingExecutor.ExecuteShardingList<T>(statements, table);
        }

        #region SelectFirst Sql

        /// <summary>
        /// 获取第一行对象
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("使用此方法需要自行在SQL中加入Limit或者Top，否则容易造成数据库崩溃！")]
        public T SelectFirst<T>(String sql) where T : class, new()
        {
            return SelectFirst<T>(sql, null);
        }

        /// <summary>
        /// 获取第一行对象
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("使用此方法需要自行在SQL中加入Limit或者Top，否则容易造成数据库崩溃！")]
        public T SelectFirst<T>(String sql, StatementParameterCollection parameters) where T : class, new()
        {
            return SelectFirst<T>(sql, parameters, null, OperationType.Default);
        }

        /// <summary>
        /// 获取第一行对象
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("使用此方法需要自行在SQL中加入Limit或者Top，否则容易造成数据库崩溃！")]
        public T SelectFirst<T>(String sql, StatementParameterCollection parameters, IDictionary hints) where T : class, new()
        {
            return SelectFirst<T>(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 获取第一行对象
        /// </summary>
        /// <typeparam name="T">对象</typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("使用此方法需要自行在SQL中加入Limit或者Top，否则容易造成数据库崩溃！")]
        public T SelectFirst<T>(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));
                T item = default(T);

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSqlStatement(table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillFirstByName(reader, table.ColumnList, ref item);
                    }
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetSqlStatement(table, LogicDbName, ShardingStrategy, sql, parameters, newHints, operationType));

                    var temp = ShardingExecutor.ExecuteShardingFirst<T>(statements, table);
                    if (temp.Count > 0) item = temp[0];
                }

                return item;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region SelectFirst IQuery

        /// <summary>
        /// 获取第一行对象
        /// </summary>
        /// <typeparam name="T">对象</typeparam>
        /// <param name="query">查询对象</param>
        /// <returns>对象</returns>
        public T SelectFirst<T>(IQuery query) where T : class, new()
        {
            return SelectFirst<T>(query, null);
        }

        /// <summary>
        /// 获取第一行对象
        /// </summary>
        /// <typeparam name="T">对象</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>对象</returns>
        public T SelectFirst<T>(IQuery query, IDictionary hints) where T : class, new()
        {
            return SelectFirst<T>(query, hints, OperationType.Default);
        }

        /// <summary>
        /// 获取第一行对象
        /// </summary>
        /// <typeparam name="T">对象</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>对象</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public T SelectFirst<T>(IQuery query, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));
                T item = default(T);

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetQueryStatement(table, LogicDbName, ShardingStrategy, query, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillFirstByName(reader, table.ColumnList, ref item);
                    }
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, null, hints,
                        newHints => SqlBuilder.GetQueryStatement(table, LogicDbName, ShardingStrategy, query, newHints, operationType));

                    var temp = ShardingExecutor.ExecuteShardingFirst<T>(statements, table);
                    if (temp.Count > 0) item = temp[0];
                }

                return item;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #endregion

        #region SelectDataReader VisitDataReader

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <param name="sql">SQL语句</param>
        /// <returns>IDataReader</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("此方法有可能造成连接泄漏，不推荐使用，建议使用VisitDataReader！")]
        public IDataReader SelectDataReader(String sql)
        {
            return SelectDataReader(sql, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>IDataReader</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("此方法有可能造成连接泄漏，不推荐使用，建议使用VisitDataReader！")]
        public IDataReader SelectDataReader(String sql, StatementParameterCollection parameters)
        {
            return SelectDataReader(sql, parameters, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令扩展属性</param>
        /// <returns>IDataReader</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("此方法有可能造成连接泄漏，不推荐使用，建议使用VisitDataReader！")]
        public IDataReader SelectDataReader(String sql, StatementParameterCollection parameters, IDictionary hints)
        {
            return SelectDataReader(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令扩展属性</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>IDataReader</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("此方法有可能造成连接泄漏，不推荐使用，建议使用VisitDataReader！")]
        public IDataReader SelectDataReader(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                Statement statement = SqlBuilder.GetSqlStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                SqlTable.AddSqlToExtendParams(statement, hints);
                return DatabaseBridge.Instance.ExecuteReader(statement);
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        /// <summary>
        /// 执行查询语句，并返回指定的结果（连接会确认被释放，安全）
        /// </summary>
        /// <typeparam name="T">返回值的类型</typeparam>
        /// <param name="sql">SQL语句</param>
        /// <param name="callback">回调，接受IDataReader作为参数，返回T类型的结果</param>
        /// <returns>T</returns>
        public T VisitDataReader<T>(String sql, Func<IDataReader, T> callback)
        {
            return VisitDataReader(sql, null, callback);
        }

        /// <summary>
        /// 执行查询语句，并返回指定的结果（连接会确认被释放，安全）
        /// </summary>
        /// <typeparam name="T">返回值的类型</typeparam>
        /// <param name="sql">SQL语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="callback">回调，接受IDataReader作为参数，返回T类型的结果</param>
        /// <returns>T</returns>
        public T VisitDataReader<T>(String sql, StatementParameterCollection parameters, Func<IDataReader, T> callback)
        {
            return VisitDataReader(sql, parameters, null, callback);
        }

        /// <summary>
        /// 执行查询语句，并返回指定的结果（连接会确认被释放，安全）
        /// </summary>
        /// <typeparam name="T">返回值的类型</typeparam>
        /// <param name="sql">SQL语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令扩展属性</param>
        /// <param name="callback">回调，接受IDataReader作为参数，返回T类型的结果</param>
        /// <returns>T</returns>
        public T VisitDataReader<T>(String sql, StatementParameterCollection parameters, IDictionary hints, Func<IDataReader, T> callback)
        {
            return VisitDataReader(sql, parameters, hints, OperationType.Default, callback);
        }

        /// <summary>
        /// 执行查询语句，并返回指定的结果（连接会确认被释放，安全）
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令扩展属性</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <param name="callback">回调，接受IDataReader作为参数，返回T类型的结果</param>
        /// <returns>T</returns>
        public T VisitDataReader<T>(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType, Func<IDataReader, T> callback)
        {
            using (var reader = SelectDataReader(sql, parameters, hints, operationType))
            {
                return callback(reader);
            }
        }

        #endregion

        #region SelectDataTable

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <returns>DataTable</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataTable SelectDataTable(String sql)
        {
            return SelectDataTable(sql, null);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>DataTable</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataTable SelectDataTable(String sql, StatementParameterCollection parameters)
        {
            return SelectDataTable(sql, parameters, null, OperationType.Default);
        }

        /// <summary>
        ///  执行查询语句
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令的扩展属性</param>
        /// <returns>DataTable</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataTable SelectDataTable(String sql, StatementParameterCollection parameters, IDictionary hints)
        {
            return SelectDataTable(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行查询语句
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令的扩展属性 </param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>DataTable</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataTable SelectDataTable(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            DataSet ds = SelectDataSet(sql, parameters, hints, operationType);
            if (ds == null)
                return null;
            return ds.Tables.Count > 0 ? ds.Tables[0] : null;
        }

        #endregion

        #region SelectDataSet

        /// <summary>
        /// 执行查询
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet SelectDataSet(String sql)
        {
            return SelectDataSet(sql, null);
        }

        /// <summary>
        /// 执行查询
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet SelectDataSet(String sql, StatementParameterCollection parameters)
        {
            return SelectDataSet(sql, parameters, null);
        }

        /// <summary>
        /// 执行查询
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令的扩展属性</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet SelectDataSet(String sql, StatementParameterCollection parameters, IDictionary hints)
        {
            return SelectDataSet(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sql">SQL语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令的扩展属性</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet SelectDataSet(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                DataSet dataSet;

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSqlStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    dataSet = DatabaseBridge.Instance.ExecuteDataSet(statement, null);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetSqlStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, newHints, operationType));
                    dataSet = ShardingExecutor.ExecuteShardingDataSet(statements);
                }

                return dataSet;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        /// <summary>
        /// 通过dataset查询
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet SelectDataSet<T>(IQuery query) where T : class, new()
        {
            return SelectDataSet<T>(query, null);
        }

        /// <summary>
        /// 通过dataset查询
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet SelectDataSet<T>(IQuery query, IDictionary hints) where T : class, new()
        {
            return SelectDataSet<T>(query, hints, OperationType.Default);
        }

        /// <summary>
        /// 通过dataset查询
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="query">查询对象</param>
        /// <param name="hints">指令参数：如timeout </param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet SelectDataSet<T>(IQuery query, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                DataSet dataSet;
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetQueryStatement(table, LogicDbName, ShardingStrategy, query, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    dataSet = DatabaseBridge.Instance.ExecuteDataSet(statement, null);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, null, hints,
                        newHints => SqlBuilder.GetQueryStatement(table, LogicDbName, ShardingStrategy, query, newHints, operationType));
                    dataSet = ShardingExecutor.ExecuteShardingDataSet(statements);
                }

                return dataSet;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region ExecScalarBySp

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="procName">存储过程名</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>object, 返回存储过程执行结果的第一行第一列，如果存储过程执行结果为0行，则返回null。</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object ExecScalarBySp(String procName, StatementParameterCollection parameters)
        {
            return ExecScalarBySp(procName, parameters, null);
        }

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="procName">存储过程名</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>object, 返回存储过程执行结果的第一行第一列，如果存储过程执行结果为0行，则返回null。</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object ExecScalarBySp(String procName, StatementParameterCollection parameters, IDictionary hints)
        {
            return ExecScalarBySp(procName, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="procName">存储过程名</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>object, 返回存储过程执行结果的第一行第一列，如果存储过程执行结果为0行，则返回null。</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object ExecScalarBySp(String procName, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                Object result = null;

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSpStatement(Table, LogicDbName, ShardingStrategy, procName, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    result = DatabaseBridge.Instance.ExecuteScalar(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetSpStatement(Table, LogicDbName, ShardingStrategy, procName, parameters, newHints, operationType));
                    var temp = ShardingExecutor.ExecuteShardingScalar(statements);
                    if (temp.Count > 0) result = temp[0];
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region ExecSp

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">查询参数</param>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public void ExecSp(String procName, StatementParameterCollection parameters)
        {
            ExecSp(procName, parameters, null);
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public void ExecSp(String procName, StatementParameterCollection parameters, IDictionary hints)
        {
            ExecSp(procName, parameters, hints, OperationType.Write);
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints"> 指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public void ExecSp(String procName, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSpNonQueryStatement(Table, LogicDbName, ShardingStrategy, procName, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    DatabaseBridge.Instance.ExecuteNonQuery(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetSpNonQueryStatement(Table, LogicDbName, ShardingStrategy, procName, parameters, newHints, operationType));
                    ShardingExecutor.ExecuteShardingNonQuery(statements);
                }
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region ExecListBySp

        /// <summary>
        /// 执行查询存储过程
        /// </summary>
        /// <typeparam name="T">结果类型</typeparam>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> ExecListBySp<T>(String procName, StatementParameterCollection parameters) where T : class, new()
        {
            return ExecListBySp<T>(procName, parameters, null);
        }

        /// <summary>
        /// 执行查询存储过程
        /// </summary>
        /// <typeparam name="T">结果类型</typeparam>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> ExecListBySp<T>(String procName, StatementParameterCollection parameters, IDictionary hints) where T : class, new()
        {
            return ExecListBySp<T>(procName, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行查询存储过程
        /// </summary>
        /// <typeparam name="T">结果类型</typeparam>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>对象集合</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public IList<T> ExecListBySp<T>(String procName, StatementParameterCollection parameters, IDictionary hints, OperationType operationType) where T : class, new()
        {
            try
            {
                LogManager.Logger.StartTracing();
                IList<T> list = new List<T>();
                var table = SqlTableFactory.Instance.Build(LogicDbName, typeof(T));

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSpStatement(table, LogicDbName, ShardingStrategy, procName, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);

                    using (var reader = DatabaseBridge.Instance.ExecuteReader(statement))
                    {
                        OrmUtil.FillByName(reader, table.ColumnList, list);
                    }
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetSpStatement(table, LogicDbName, ShardingStrategy, procName, parameters, newHints, operationType));

                    list = ShardingExecutor.ExecuteShardingList<T>(statements, table);
                }

                return list;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region ExecDataSetBySp

        /// <summary>
        /// 通过存储过程执行dataset
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">全部存储过程参数</param>
        /// <returns>DataSet</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet ExecDataSetBySp(String procName, StatementParameterCollection parameters)
        {
            return ExecDataSetBySp(procName, parameters, null);
        }

        /// <summary>
        /// 通过存储过程执行dataset
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>DataSet</returns>
        public DataSet ExecDataSetBySp(String procName, StatementParameterCollection parameters, IDictionary hints)
        {
            return ExecDataSetBySp(procName, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 通过存储过程执行dataset
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="hints">指令参数：如timeout </param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>dataset</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataSet ExecDataSetBySp(String procName, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                DataSet dataSet;

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetSpStatement(Table, LogicDbName, ShardingStrategy, procName, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    dataSet = DatabaseBridge.Instance.ExecuteDataSet(statement, null);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetSpStatement(Table, LogicDbName, ShardingStrategy, procName, parameters, newHints, operationType));

                    dataSet = ShardingExecutor.ExecuteShardingDataSet(statements);
                }

                return dataSet;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region ExecDataTableBySp

        /// <summary>
        /// 通过存储过程执行DataTable
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">全部存储过程参数</param>
        /// <returns>DataTable</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataTable ExecDataTableBySp(String procName, StatementParameterCollection parameters)
        {
            return ExecDataTableBySp(procName, parameters, null);
        }

        /// <summary>
        /// 通过存储过程执行DataTable
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">全部存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>DataTable</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataTable ExecDataTableBySp(String procName, StatementParameterCollection parameters, IDictionary hints)
        {
            return ExecDataTableBySp(procName, parameters, hints, OperationType.Default);
        }

        /// <summary>
        ///  通过存储过程执行DataTable
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">全部存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>DataTable</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public DataTable ExecDataTableBySp(String procName, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            DataSet ds = ExecDataSetBySp(procName, parameters, hints, operationType);
            if (ds == null)
                return null;
            return ds.Tables.Count > 0 ? ds.Tables[0] : null;
        }

        #endregion

        #region ExecDataReaderBySp VisitDataReaderBySp

        /// <summary>
        /// 通过存储过程执行DataReader
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <returns>DataReader</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("此方法有可能造成连接泄漏，不推荐使用，建议使用VisitDataReaderBySP！")]
        public IDataReader ExecDataReaderBySp(String procName, StatementParameterCollection parameters)
        {
            return ExecDataReaderBySp(procName, parameters, null);
        }

        /// <summary>
        /// 通过存储过程执行DataReader
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>IDataReader</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("此方法有可能造成连接泄漏，不推荐使用，建议使用VisitDataReaderBySP！")]
        public IDataReader ExecDataReaderBySp(String procName, StatementParameterCollection parameters, IDictionary hints)
        {
            return ExecDataReaderBySp(procName, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 通过存储过程执行DataReader
        /// </summary>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>IDataReader</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        [Obsolete("此方法有可能造成连接泄漏，不推荐使用，建议使用VisitDataReaderBySP！")]
        public IDataReader ExecDataReaderBySp(String procName, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                Statement statement = SqlBuilder.GetSpStatement(Table, LogicDbName, ShardingStrategy, procName, parameters, hints, operationType);
                SqlTable.AddSqlToExtendParams(statement, hints);
                return DatabaseBridge.Instance.ExecuteReader(statement);
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        /// <summary>
        /// 通过存储过程执行DataReader，并返回结果（null，0或者指定的结果类型）
        /// </summary>
        /// <typeparam name="T">返回类型</typeparam>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="callback">回调，接受IDataReader为参数，返回指定的类型结果</param>
        /// <returns>T的实例，可能为null或者0</returns>
        public T VisitDataReaderBySp<T>(String procName, StatementParameterCollection parameters, Func<IDataReader, T> callback)
        {
            return VisitDataReaderBySp(procName, parameters, null, callback);
        }

        /// <summary>
        /// 通过存储过程执行DataReader，并返回结果（null，0或者指定的结果类型）
        /// </summary>
        /// <typeparam name="T">返回类型</typeparam>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="callback">回调，接受IDataReader为参数，返回指定的类型结果</param>
        /// <returns>T的实例，可能为null或者0</returns>
        public T VisitDataReaderBySp<T>(String procName, StatementParameterCollection parameters, IDictionary hints, Func<IDataReader, T> callback)
        {
            return VisitDataReaderBySp(procName, parameters, hints, OperationType.Default, callback);
        }

        /// <summary>
        /// 通过存储过程执行DataReader，并返回结果（null，0或者指定的结果类型）
        /// </summary>
        /// <typeparam name="T">返回类型</typeparam>
        /// <param name="procName">存储过程名称</param>
        /// <param name="parameters">存储过程参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <param name="callback">回调，接受IDataReader为参数，返回指定的类型结果</param>
        /// <returns>T的实例，可能为null或者0</returns>
        public T VisitDataReaderBySp<T>(String procName, StatementParameterCollection parameters, IDictionary hints, OperationType operationType, Func<IDataReader, T> callback)
        {
            using (var reader = ExecDataReaderBySp(procName, parameters, hints, operationType))
            {
                return callback(reader);
            }
        }

        #endregion

        #region ExecScalar

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <returns>object</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object ExecScalar(String sql)
        {
            return ExecScalar(sql, null);
        }

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>object</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object ExecScalar(String sql, StatementParameterCollection parameters)
        {
            return ExecScalar(sql, parameters, null);
        }

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>object</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object ExecScalar(String sql, StatementParameterCollection parameters, IDictionary hints)
        {
            return ExecScalar(sql, parameters, hints, OperationType.Default);
        }

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>object</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Object ExecScalar(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                Object result = null;

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetScalarStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    result = DatabaseBridge.Instance.ExecuteScalar(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetScalarStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, newHints, operationType));

                    var temp = ShardingExecutor.ExecuteShardingScalar(statements);

                    if (temp.Count > 0)
                    {
                        if (temp.Count == 1)
                        {
                            result = temp[0];
                        }
                        else
                        {
                            throw new DalException("ExecScalar exception:more than one shard.");
                        }
                    }
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

        #region ExecNonQuery

        /// <summary>
        /// 执行非查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 ExecNonQuery(String sql)
        {
            return ExecNonQuery(sql, null);
        }

        /// <summary>
        /// 执行非查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 ExecNonQuery(String sql, StatementParameterCollection parameters)
        {
            return ExecNonQuery(sql, parameters, null);
        }

        /// <summary>
        /// 执行非查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 ExecNonQuery(String sql, StatementParameterCollection parameters, IDictionary hints)
        {
            return ExecNonQuery(sql, parameters, hints, OperationType.Write);
        }

        /// <summary>
        /// 执行非查询指令
        /// </summary>
        /// <param name="sql">sql语句</param>
        /// <param name="parameters">查询参数</param>
        /// <param name="hints">指令参数：如timeout</param>
        /// <param name="operationType">操作类型，读写分离，默认从master库读取</param>
        /// <returns>影响行数</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        public Int32 ExecNonQuery(String sql, StatementParameterCollection parameters, IDictionary hints, OperationType operationType)
        {
            try
            {
                LogManager.Logger.StartTracing();
                Int32 result;

                if (!IsShardEnabled)
                {
                    Statement statement = SqlBuilder.GetNonQueryStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, hints, operationType);
                    SqlTable.AddSqlToExtendParams(statement, hints);
                    result = DatabaseBridge.Instance.ExecuteNonQuery(statement);
                }
                else
                {
                    var statements = ShardingUtil.GetShardStatement(LogicDbName, ShardingStrategy, parameters, hints,
                        newHints => SqlBuilder.GetNonQueryStatement(Table, LogicDbName, ShardingStrategy, sql, parameters, newHints));

                    result = ShardingExecutor.ExecuteShardingNonQuery(statements).Sum();
                }

                return result;
            }
            catch (Exception ex)
            {
                LogManager.Logger.TracingError(ex);
                throw;
            }
            finally
            {
                LogManager.Logger.StopTracing();
            }
        }

        #endregion

    }
}
