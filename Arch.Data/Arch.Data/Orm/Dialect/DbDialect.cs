using Arch.Data.Common.Enums;
using Arch.Data.Orm.sql;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;

namespace Arch.Data.Orm.Dialect
{
    /// <summary>
    /// 
    /// </summary>
    public abstract class DbDialect : IDbDialect
    {
        #region Implementation of IDbDialect

        /// <summary>
        /// 开始引号
        /// </summary>
        public virtual Char OpenQuote
        {
            get { return '['; }
        }

        /// <summary>
        /// 结束引号
        /// </summary>
        public virtual Char CloseQuote
        {
            get { return ']'; }
        }

        /// <summary>
        /// 标识返回自增长id
        /// </summary>
        public virtual String IdentitySelectString
        {
            get { return String.Empty; }
        }

        /// <summary>
        /// 拼接参数
        /// </summary>
        public virtual String QuoteParameter(String parameterName)
        {
            return String.Concat("@", parameterName);
        }

        /// <summary>
        /// 拼接参数
        /// </summary>
        public virtual String QuotePKParameter(String parameterName)
        {
            return String.Concat("@pk", parameterName);
        }

        public virtual String QuotePrefixParameter(String parameterName)
        {
            return String.Concat("@P_", parameterName);
        }

        /// <summary>
        /// 拼接名称
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public virtual String Quote(String name)
        {
            return String.Concat(OpenQuote, name, CloseQuote);
        }

        /// <summary>
        /// 拼接操作头部
        /// </summary>
        /// <returns></returns>
        public virtual String QuoteOpenOpName(String name)
        {
            return String.Empty;
        }

        /// <summary>
        /// 拼接操作尾部
        /// </summary>
        /// <returns></returns>
        public virtual String QuoteCloseOpName()
        {
            return String.Empty;
        }

        /// <summary>
        /// 加锁类型值
        /// </summary>
        /// <returns></returns>
        public virtual String WithLock(String parameterName)
        {
            return parameterName;
        }

        #endregion

        public abstract String LimitPrefix(Int32 from, Int32 to);

        public abstract String LimitSuffix(Int32 from, Int32 to);

        public abstract String PagingPrefix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending);

        public abstract String PagingSuffix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending, IList<SqlColumn> sqlColumns);
    }

    /// <summary>
    /// 方言工厂
    /// </summary>
    public static class DbDialectFactory
    {
        private static ConcurrentDictionary<DatabaseProviderType, DbDialect> DbDialects = new ConcurrentDictionary<DatabaseProviderType, DbDialect>();

        /// <summary>
        /// 构建方言
        /// </summary>
        /// <param name="providerType">数据库类型</param>
        /// <returns></returns>
        public static DbDialect Build(DatabaseProviderType providerType)
        {
            return DbDialects.GetOrAdd(providerType, type =>
            {
                switch (type)
                {
                    case DatabaseProviderType.MySql:
                        return new MySqlDialect();
                    case DatabaseProviderType.SqlServer:
                        return new SqlServer2008Dialect();
                }

                return null;
            });
        }
    }

}
