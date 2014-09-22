using System;
using System.Data.Common;
using System.Data.SqlClient;
using System.Data;

namespace platform.dao.providers
{
    /// <summary>
    /// Sql Server 数据库提供者实现
    /// </summary>
    public sealed class SqlDatabaseProvider : IDatabaseProvider
    {
        /// <summary>
        /// 创建数据库链接
        /// </summary>
        /// <returns>数据库链接</returns>
        public DbConnection CreateConnection()
        {
            return new SqlConnection();
        }

        /// <summary>
        /// 创建数据库指令
        /// </summary>
        /// <returns>数据库指令</returns>
        public DbCommand CreateCommand()
        {
            return new SqlCommand();
        }

        /// <summary>
        /// 创建数据库参数合法名称
        /// </summary>
        /// <param name="name">数据库参数名称</param>
        /// <returns>据库参数合法名称</returns>
        public string CreateParameterName(string name)
        {
            if (string.IsNullOrEmpty(name))
                throw new ArgumentNullException("name");

            if (name[0] != '@')
            {
                return "@" + name;
            }
            return name;
        }

        /// <summary>
        /// 创建数据库适配器
        /// </summary>
        /// <returns>数据库适配器</returns>
        public DbDataAdapter CreateDataAdapter()
        {
            return new SqlDataAdapter();
        }

        /// <summary>
        /// 是否支持导出数据库指令参数
        /// </summary>
        public bool DeriveParametersSupported
        {
            get
            {
                return true;
            }
        }

        /// <summary>
        /// 导出数据库指令参数
        /// </summary>
        /// <param name="command">数据库指令</param>
        public void DeriveParameters(DbCommand command)
        {
            if (command == null)
                throw new ArgumentNullException("command");
            if (command.GetType() != typeof(SqlCommand))
                throw new ArgumentException("command must be SqlCommand!");

            SqlCommandBuilder.DeriveParameters((SqlCommand)command);

            foreach (DbParameter para in command.Parameters)
            {
                if (para.Direction == ParameterDirection.InputOutput || para.Direction == ParameterDirection.Output)
                {
                    para.Value = DBNull.Value;
                }
            }
        }

        public string ProviderType
        {
            get { return "SQLSERVER2008"; }
        }
    }
}
