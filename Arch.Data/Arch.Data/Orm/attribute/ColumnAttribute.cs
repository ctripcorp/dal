using System;
using System.Data;

namespace Arch.Data.Orm
{
    /// <summary>
    /// 表示一个数据库中的字段
    /// </summary>
    [AttributeUsage(AttributeTargets.Property | AttributeTargets.Field, Inherited = false, AllowMultiple = false)]
    public class ColumnAttribute : Attribute
    {
        /// <summary>
        /// 字段名
        /// </summary>
        public String Name { get; set; }

        /// <summary>
        /// 字段别名
        /// </summary>
        public String Alias { get; set; }

        /// <summary>
        /// 字段所对应的数据库类型
        /// </summary>
        public DbType ColumnType
        {
            get { return NullableColumnType ?? DbType.AnsiString; }
            set { NullableColumnType = value; }
        }

        /// <summary>
        /// 查询ORM时的默认值，不支持NULL作为默认值
        /// </summary>
        public Object DefaultValue { get; set; }

        /// <summary>
        /// 
        /// </summary>
        public DbType? NullableColumnType { get; private set; }

        /// <summary>
        /// 字段的大小
        /// </summary>
        public Int32 Length { get; set; }

    }
}
