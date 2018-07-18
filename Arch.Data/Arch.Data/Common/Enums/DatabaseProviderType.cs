using System;

namespace Arch.Data.Common.Enums
{
    /// <summary>
    /// 数据库类别，如MySql或者Sql Server等
    /// </summary>
    public enum DatabaseProviderType
    {
        MySql,
        SqlServer
    }

    public class DatabaseProviderTypeFactory
    {
        /// <summary>
        /// 从字符串获取数据库类型
        /// </summary>
        /// <param name="providerType"></param>
        /// <returns></returns>
        public static DatabaseProviderType GetProviderType(String providerType)
        {
            switch (providerType.ToUpper())
            {
                case "MYSQL":
                    return DatabaseProviderType.MySql;
                case "SQLSERVER2008":
                    return DatabaseProviderType.SqlServer;
                default:
                    throw new NotSupportedException();
            }
        }
    }

}
