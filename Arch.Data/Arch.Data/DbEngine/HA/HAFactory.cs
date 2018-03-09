using Arch.Data.Common.Enums;
using System;

namespace Arch.Data.DbEngine.HA
{
    class HAFactory
    {
        /// <summary>
        /// 获取HA对象
        /// </summary>
        /// <param name="logicDbName"></param>
        /// <returns></returns>
        public static IHA GetInstance(String logicDbName)
        {
            var providerType = DALBootstrap.GetProviderType(logicDbName);

            switch (providerType)
            {
                case DatabaseProviderType.SqlServer:
                    return new SqlServerHA();
                case DatabaseProviderType.MySql:
                    return new MySqlHA();
                default:
                    throw new NotImplementedException("Not supported.");
            }
        }

    }
}
