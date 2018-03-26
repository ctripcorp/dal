
namespace Arch.Data.Common.Enums
{
    /// <summary>
    /// Shard操作的类型，详细见枚举描述
    /// </summary>
    public enum ShardingType
    {
        /// <summary>
        /// 默认，多个相同名称的表，分散在多个数据库中
        /// </summary>
        ShardByDB = 0,
        /// <summary>
        /// 所有表位于同一数据库中，每个表名均不相同
        /// </summary>
        ShardByTable = 1,
        /// <summary>
        /// 所有表分散在多个数据库中，每个表名均不相同
        /// </summary>
        ShardByDBAndTable = 2
    }
}
