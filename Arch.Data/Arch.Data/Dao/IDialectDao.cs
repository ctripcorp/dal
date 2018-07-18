using Arch.Data.Orm.partially;
using System;
using System.Collections;
using System.Collections.Generic;

namespace Arch.Data
{
    public interface IDialectDao
    {
        /// <summary>
        /// 批量替换对象
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
        ///   baseDao.BulkReplace(list);</para>
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.batchSize最大10000，建议100，分批替换。</para> 
        /// <para> 2.该批量替换推荐在mysql中使用，如果MsSQL推荐用表变量方式处理</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="list">对象集合</param>
        /// <returns>是否成功</returns>
        /// <exception cref="DalException">数据访问异常</exception>
        Boolean BulkReplace<T>(IList<T> list) where T : class ,new();

        /// <summary>
        /// 批量替换对象
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
        ///   baseDao.BulkReplace(list);</para>
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.batchSize最大10000，建议100，分批替换。</para> 
        /// <para> 2.该批量替换推荐在mysql中使用，如果MsSQL推荐用表变量方式处理</para> 
        /// <para> 3.扩展属性的key，具体可以查询DALExtStatementConstant的常量列表（譬如：timeout等）</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="list">对象集合</param>
        /// <param name="hints">扩展属性，譬如:timeout等</param>
        /// <returns>是否成功</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        Boolean BulkReplace<T>(IList<T> list, IDictionary hints) where T : class ,new();

        /// <summary>
        /// 批量替换对象,batchSize：100
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
        /// <para>备注:批量替换对象</para>
        /// <para> 1.batchSize最大10000，建议100，分批插入。</para> 
        /// <para> 2.该批量替换推荐在mysql中使用，如果MsSQL推荐用表变量方式处理</para> 
        /// <para> 3.扩展属性的key，具体可以查询DALExtStatementConstant的常量列表（譬如：timeout等）</para>
        /// <para> 4.可以通过指定的逻辑数据库名插入数据，也就是一个dao(数据访问对象)支持多个数据库连接</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="logicDbName">逻辑数据库名</param>
        /// <param name="list">对象集合</param>
        /// <param name="hints">扩展属性，譬如:timeout等 </param>
        /// <returns>是否成功</returns>
        /// <remarks>1.集合对象不能超过10000, 2.SQLServer参数不能超过2100, Mysql控制报文最大长度</remarks>
        /// <exception cref="DalException">数据访问框架异常</exception>
        Boolean BulkReplace<T>(String logicDbName, IList<T> list, IDictionary hints) where T : class ,new();

        /// <summary>
        /// 替换对象 
        /// <example><para>示例：</para>
        /// <code>
        /// readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("your databaseSetName");
        ///  City c = new City
        ///                 {
        ///                     Name = "test",
        ///                     Population = "test",
        ///                    Country1Code = "test",
        ///                     District = "test"
        ///                 };
        /// baseDao.Replace(city);
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.实体类打上了[ID]这个标签，替换成功将返回新增的主键值</para> 
        /// <para> 2.实体类打上了[PK]这个标签，(替换成功将返回新增的主键值，如果PK不为ID，则返回影响条数)</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="obj">实体对象</param>
        /// <returns>返回第一列值（譬如自增长ID）</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        Object Replace<T>(T obj) where T : class, new();

        /// <summary>
        /// 替换对象
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
        ///  IDictionary statementParams = new  new Hashtable();
        ///  statementParams.Add(DALExtStatementConstant.TIMEOUT, "1000");
        ///  baseDao.Replace(city,statementParams);
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.实体类打上了[ID]这个标签，替换成功将返回新增的主键值</para> 
        /// <para> 2.实体类打上了[PK]这个标签，(替换成功将返回新增的主键值，如果PK不为ID，则返回影响条数)</para> 
        /// <para> 3.扩展属性的key，具体可以查询DALExtStatementConstant的常量列表（譬如：timeout等）</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="obj">实体对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout）</param>
        /// <returns>返回第一列值（譬如自增长ID）</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        Object Replace<T>(T obj, IDictionary hints) where T : class, new();

        /// <summary>
        /// 替换对象
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
        ///  baseDao.Replace("yourdatabaseSetName",city,statementParams);
        /// </code>
        /// </example>
        /// <remarks>
        /// <para>备注:</para>
        /// <para> 1.实体类打上了[ID]这个标签，替换成功将返回新增的主键值</para> 
        /// <para> 2.实体类打上了[PK]这个标签，(替换成功将返回新增的主键值，如果PK不为ID，则返回影响条数)</para> 
        /// <para> 3.扩展属性的key，具体可以查询DALExtStatementConstant的常量列表（譬如：timeout等）</para> 
        /// <para> 4.可以通过指定的逻辑数据库名插入数据，也就是一个dao(数据访问对象)支持多个数据库连接</para> 
        /// </remarks>
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="logicDbName">逻辑数据库名</param>
        /// <param name="obj">实体对象</param>
        /// <param name="hints">指令扩展属性（分片：shardcol,超时:timeout） </param>
        /// <returns>返回第一列值（譬如自增长ID）</returns>
        /// <exception cref="DalException">数据访问框架异常</exception>
        Object Replace<T>(String logicDbName, T obj, IDictionary hints) where T : class, new();

        /// <summary>
        /// 部分替换
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="replacePartial">IReplacePartial 对象</param>
        /// <param name="obj">实体对象</param>
        /// <returns></returns>
        Object ReplacePartially<T>(IReplacePartial<T> replacePartial, T obj) where T : class, new();

        /// <summary>
        /// 部分替换
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="replacePartial">IReplacePartial 对象</param>
        /// <param name="obj">实体对象</param>
        /// <param name="hints">扩展参数(Hints)</param>
        /// <returns></returns>
        Object ReplacePartially<T>(IReplacePartial<T> replacePartial, T obj, IDictionary hints) where T : class, new();

        /// <summary>
        /// 部分替换
        /// </summary>
        /// <typeparam name="T">实体类型</typeparam>
        /// <param name="logicDbName">DatabaseSet 名称</param>
        /// <param name="replacePartial">IReplacePartial 对象</param>
        /// <param name="obj">实体对象</param>
        /// <param name="hints">扩展参数(Hints)</param>
        /// <returns></returns>
        Object ReplacePartially<T>(String logicDbName, IReplacePartial<T> replacePartial, T obj, IDictionary hints) where T : class, new();

        /// <summary>
        /// 获取 ReplacePartially 对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <returns></returns>
        IReplacePartial<T> GetReplacePartially<T>() where T : class, new();
    }
}
