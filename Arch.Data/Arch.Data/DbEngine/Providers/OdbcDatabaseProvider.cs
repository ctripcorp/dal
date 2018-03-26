using System;
using System.Data.Common;
using System.Data.Odbc;

namespace Arch.Data.DbEngine.Providers
{
    public sealed class OdbcDatabaseProvider : IDatabaseProvider
    {
        /// <summary>
        /// 创建数据库链接
        /// </summary>
        /// <returns>数据库链接</returns>
        public DbConnection CreateConnection()
        {
            return new OdbcConnection();
        }

        /// <summary>
        /// 创建数据库指令
        /// </summary>
        /// <returns>数据库指令</returns>
        public DbCommand CreateCommand()
        {
            return new OdbcCommand();
        }

        /// <summary>
        /// 创建数据库参数合法名称
        /// </summary>
        /// <param name="name">数据库参数名称</param>
        /// <returns>据库参数合法名称</returns>
        public String CreateParameterName(String name)
        {
            return name;
        }

        /// <summary>
        /// 创建数据库适配器
        /// </summary>
        /// <returns>数据库适配器</returns>
        public DbDataAdapter CreateDataAdapter()
        {
            return new OdbcDataAdapter();
        }

        /// <summary>
        /// 是否支持导出数据库指令参数
        /// </summary>
        public Boolean DeriveParametersSupported
        {
            get { return false; }
        }

        /// <summary>
        /// 导出数据库指令参数
        /// </summary>
        /// <param name="command">数据库指令</param>
        public void DeriveParameters(DbCommand command)
        {
            throw new NotSupportedException("Odbc db command does't support derive parameters!");
        }

        public String ProviderType
        {
            get { return "ODBC"; }
        }
    }
}
