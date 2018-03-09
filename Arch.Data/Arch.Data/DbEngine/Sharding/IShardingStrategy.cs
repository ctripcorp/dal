using Arch.Data.DbEngine.Configuration;
using Arch.Data.Orm.sql;
using System;
using System.Collections;
using System.Collections.Generic;

//设计原则：切换到DAS后，直连阶段的代码无需任何修改，可以照常工作

//表切分(a类型提供直接支持，其他的不提供直接的支持，需要用户在操作的时候区分开来)：
//    a. 表结构相同，切分和的表名相同，切分后的表位于不同的数据库中
//    b. 表结构相同，切分后的表名不同，切分后的表在同一数据库中
//    c. 表结构相同，切分后的表名不同，切分后的表在不同的数据库中 
//    d. 单一表中字段太多，将一些字段移动到新的表中
//数据库切分：
//    a. 单一数据库中表的数量太多，将一些表移动到其他数据库中，框架不需要额外的操作，将数据库配置好即可

//1. 单条增删改：
//    直接根据条件，判断落在哪个Shard
//    如果增删改操作有Cross-Shard，目前是直接报错，未来怎么做？
//    建议用户先将操作进行分解，然后分别落向单个Shard进行增删改操作
//2. 批量增删改：
//    首先对所有数据进行一次Shuffle，然后分为多个Bulk操作落向多个Shard
//3. 查询,单Shard操作，直接返回, Cross-Shard, tricky
//    a. Sharding依赖于字段，In，Between等查询条件不会直接影响落向哪个Shard，目前的Sharding策略需要用户显示传入Sharding字段的值，即使查询中没有此字段返回或者作为查询条件
//    b. 
namespace Arch.Data.DbEngine.Sharding
{
    /// <summary>
    /// 新的Sharding策略：
    /// 1. BaseDao中的API均针对单个Shard，接收ShardID参数
    /// 
    /// 两种情形：
    /// 1. 可以根据Shard字段计算出位于哪些Shard
    /// 2. 无法知晓哪些Shard：落向所有Shard（性能？）
    /// 
    /// 每个DatabaseSet一个Strategy
    /// </summary>
    public interface IShardingStrategy
    {
        /// <summary>
        /// 所有Shard列表
        /// </summary>
        IList<String> AllShards { get; }

        /// <summary>
        /// Shard字段列表，通常一个库中有多个表，可能一个表采用orderid做Shard，另一个表采用cityid做Shard，这里提供这种支持
        /// </summary>
        IList<String> ShardColumns { get; }

        IDictionary<String, String> ShardColumnAndTable { get; }

        /// <summary>
        /// 分库，如果配置了Sharding，默认为True，可以显示通过shardByDB=false设置为False
        /// </summary>
        // ReSharper disable once InconsistentNaming
        Boolean ShardByDB { get; }

        /// <summary>
        /// 分表，默认为False,可以显示通过shardByTable=true设置为True
        /// </summary>
        Boolean ShardByTable { get; }

        /// <summary>
        /// 初始化Sharding策略
        /// </summary>
        /// <param name="config"></param>
        /// <param name="databaseSet"></param>
        void SetShardConfig(IDictionary<String, String> config, DatabaseSetElement databaseSet);

        /// <summary>
        /// 将数据按照当前的策略进行分组，返回ShardID：T的键值对，主要用于增删改
        /// </summary>
        /// <typeparam name="T">需要Shuffle的类型</typeparam>
        /// <typeparam name="TColumnType">Sharding的字段</typeparam>
        /// <param name="dataList">需要Shuffle的数据</param>
        /// <param name="shuffleByColumn"></param>
        /// <returns>ShardID：T的键值对</returns>
        IDictionary<String, IList<T>> ShuffleData<T, TColumnType>(IList<T> dataList, Func<T, TColumnType> shuffleByColumn) where TColumnType : IComparable;

        /// <summary>
        /// 如果Sql语句中有Between，计算出当前Sql语句需要在哪几个Shard上进行操作
        /// </summary>
        /// <typeparam name="TColumnType"></typeparam>
        /// <param name="start"></param>
        /// <param name="end"></param>
        /// <returns></returns>
        IList<String> ComputeShardIdsBetween<TColumnType>(TColumnType start, TColumnType end) where TColumnType : IComparable;

        /// <summary>
        /// 如果Sql语句中有In，计算出当前Sql语句需要在哪几个Shard上进行操作
        /// </summary>
        /// <typeparam name="TColumnType"></typeparam>
        /// <param name="columnValues"></param>
        /// <returns></returns>
        IList<String> ComputeShardIdsIn<TColumnType>(IList<TColumnType> columnValues) where TColumnType : IComparable;

        /// <summary>
        /// 如果Sql语句中有In，计算出当前Sql语句需要在哪几个Shard上进行操作
        /// </summary>
        /// <typeparam name="TColumnType"></typeparam>
        /// <param name="columnValues"></param>
        /// <returns></returns>
        IList<String> ComputeShardIdsIn<TColumnType>(params TColumnType[] columnValues) where TColumnType : IComparable;

        /// <summary>
        /// 根据当前策略，计算出当前字段属于哪个Shard
        /// </summary>
        /// <typeparam name="TColumnType"></typeparam>
        /// <param name="columnValue"></param>
        /// <returns></returns>
        String ComputeShardId<TColumnType>(TColumnType columnValue) where TColumnType : IComparable;

        IComparable GetShardColumnValue<T>(String logicDbName, T entity, ICollection<SqlColumn> columns, StatementParameterCollection parameters, IDictionary hints);

    }
}
