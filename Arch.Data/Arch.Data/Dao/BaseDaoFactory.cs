using Arch.Data.Common.Enums;
using Arch.Data.DbEngine;
using System;
using System.Collections.Concurrent;

namespace Arch.Data
{
    /// <summary>
    /// 创建basedao工厂
    /// </summary>
    public static class BaseDaoFactory
    {
        static readonly ConcurrentDictionary<String, BaseDao> BaseDaos = new ConcurrentDictionary<String, BaseDao>();
        static readonly ConcurrentDictionary<String, IDialectDao> DialectDaos = new ConcurrentDictionary<String, IDialectDao>();

        /// <summary>
        /// 创建basedao工厂
        /// </summary>
        /// <param name="logicDbName">数据库逻辑名称</param>
        /// <returns>BaseDao</returns>
        public static BaseDao CreateBaseDao(String logicDbName)
        {
            return BaseDaos.GetOrAdd(logicDbName, key => new BaseDao(key));
        }

        /// <summary>
        /// 获取方言DAO，请注意返回null的情况
        /// </summary>
        /// <param name="logicDbName"></param>
        /// <returns>IDialectDao</returns>
        public static IDialectDao CreateDialectDao(String logicDbName)
        {
            return DialectDaos.GetOrAdd(logicDbName, key =>
            {
                var providerType = DALBootstrap.GetProviderType(logicDbName);
                switch (providerType)
                {
                    case DatabaseProviderType.MySql:
                        return new MySqlDialectDao(key);
                    default:
                        return null;
                }
            });
        }

    }
}
