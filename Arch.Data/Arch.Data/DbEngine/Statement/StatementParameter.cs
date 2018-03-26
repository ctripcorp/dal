using System;
using System.Data;

namespace Arch.Data.DbEngine
{
    /// <summary>
    /// 指令参数
    /// </summary>
    public sealed class StatementParameter
    {
        /// <summary>
        /// 数据类型
        /// </summary>
        public DbType DbType { get; set; }

        /// <summary>
        /// 扩展数据类型,例如sqldbtype
        /// </summary>
        public Object ExtendTypeValue { get; set; }

        /// <summary>
        /// 1: sqltype
        /// </summary>
        public Int32 ExtendType { get; set; }

        public StatementParameter()
        {
            Direction = ParameterDirection.Input;
        }

        /// <summary>
        /// 参数方向
        /// </summary>
        public ParameterDirection Direction { get; set; }

        /// <summary>
        /// 是否可空
        /// </summary>
        public Boolean IsNullable { get; set; }

        /// <summary>
        /// 参数名称
        /// </summary>
        public String Name { get; set; }

        /// <summary>
        ///  TVP专用，TVP的类型名称，可以与Name一致
        /// </summary>
        public String TypeName { get; set; }

        /// <summary>
        /// 大小
        /// </summary>
        public Int32 Size { get; set; }

        /// <summary>
        /// 参数值
        /// </summary>
        public Object Value { get; set; }

        /// <summary>
        /// 参数是否为敏感数据
        /// </summary>
        public Boolean IsSensitive { get; set; }

        /// <summary>
        /// 是否是Sharding字段
        /// </summary>
        public Boolean IsShardingColumn { get; set; }

    }
}
