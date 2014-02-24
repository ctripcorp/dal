using System.Data.Common;

namespace platform.dao.providers
{
    public interface IDatabaseProvider
    {
        /// <summary>
        /// 数据库提供者类型
        /// </summary>
        string ProviderType { get; }

        /// <summary>
        /// 创建数据库链接
        /// </summary>
        /// <returns>数据库链接</returns>
        DbConnection CreateConnection();

        /// <summary>
        /// 创建数据库指令
        /// </summary>
        /// <returns>数据库指令</returns>
        DbCommand CreateCommand();

        /// <summary>
        /// 创建数据库适配器
        /// </summary>
        /// <returns></returns>
        DbDataAdapter CreateDataAdapter();

        /// <summary>
        /// 将名称变为数据库相关的参数名称
        /// </summary>
        /// <param name="name">参数名称</param>
        /// <returns>数据库相关的参数名称</returns>
        string CreateParameterName(string name);

        /// <summary>
        /// 是否支持参数导出
        /// </summary>
        bool DeriveParametersSupported { get; }

        /// <summary>
        /// 导出数据库指令参数
        /// </summary>
        /// <param name="command">数据库指令</param>
        void DeriveParameters(DbCommand command);
    }
}
