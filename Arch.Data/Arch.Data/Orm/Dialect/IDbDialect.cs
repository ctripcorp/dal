using Arch.Data.Orm.sql;
using System;
using System.Collections.Generic;

namespace Arch.Data.Orm.Dialect
{
    /// <summary>
    /// 数据库方言接口
    /// </summary>
    public interface IDbDialect
    {
        /// <summary>
        /// 结束
        /// </summary>
        Char OpenQuote { get; }

        /// <summary>
        /// 结束引号
        /// </summary>
        Char CloseQuote { get; }

        /// <summary>
        /// 标识返回自增长id
        /// </summary>
        String IdentitySelectString { get; }

        /// <summary>
        /// 拼接参数
        /// </summary>
        String QuoteParameter(String parameterName);

        /// <summary>
        /// 拼接参数
        /// </summary>
        // ReSharper disable once InconsistentNaming
        String QuotePKParameter(String parameterName);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="parameterName"></param>
        /// <returns></returns>
        String QuotePrefixParameter(String parameterName);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        String Quote(String name);

        /// <summary>
        /// 拼接操作头部
        /// </summary>
        /// <returns></returns>
        String QuoteOpenOpName(String name);

        /// <summary>
        /// 拼接操作尾部
        /// </summary>
        /// <returns></returns>
        String QuoteCloseOpName();

        /// <summary>
        /// 加nolock
        /// </summary>
        /// <returns></returns>
        String WithLock(String paramName);

        /// <summary>
        /// 如果是Top，取用from，拼接
        /// </summary>
        /// <param name="from"></param>
        /// <param name="to"></param>
        /// <returns></returns>
        String LimitPrefix(Int32 from, Int32 to);

        /// <summary>
        /// 如果是Limit，取用from和to拼接
        /// </summary>
        /// <param name="from"></param>
        /// <param name="to"></param>
        /// <returns></returns>
        String LimitSuffix(Int32 from, Int32 to);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="pageNumber"></param>
        /// <param name="pageSize"></param>
        /// <param name="orderColumnName"></param>
        /// <param name="isAscending"></param>
        /// <returns></returns>
        String PagingPrefix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="pageNumber"></param>
        /// <param name="pageSize"></param>
        /// <param name="orderColumnName"></param>
        /// <param name="isAscending"></param>
        /// <param name="sqlColumns"></param>
        /// <returns></returns>
        String PagingSuffix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending, IList<SqlColumn> sqlColumns);
    }
}
