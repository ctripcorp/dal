using System.ComponentModel;

namespace Arch.Data.Common.Enums
{
    /// <summary>
    /// 物理数据库类型
    /// </summary>
    public enum DatabaseType
    {
        /// <summary>
        /// 主数据库，可读可写
        /// </summary>
        [Description("主数据库")]
        Master = 0,
        /// <summary>
        /// 从数据库，可读
        /// </summary>
        [Description("从数据库")]
        Slave = 1
    }
}