using System;
using System.Collections.ObjectModel;
using System.Data;

namespace Arch.Data.DbEngine
{
    /// <summary>
    /// 指令参数
    /// </summary>
    public sealed class StatementParameterCollection : KeyedCollection<String, StatementParameter>
    {
        public StatementParameterCollection() : base(StringComparer.CurrentCultureIgnoreCase) { }

        /// <summary>
        /// 添加Input类型的参数
        /// </summary>
        /// <param name="name">参数名，建议有@符号，如果没有，框架会补偿</param>
        /// <param name="dbType">DbType</param>
        /// <param name="value">参数值</param>
        public void AddInParameter(String name, DbType dbType)
        {
            AddInParameter(name, dbType, null);
        }

        /// <summary>
        /// 添加Input类型的参数
        /// </summary>
        /// <param name="name">参数名，建议有@符号，如果没有，框架会补偿</param>
        /// <param name="dbType">DbType</param>
        /// <param name="value">参数值</param>
        public void AddInParameter(String name, DbType dbType, Object value)
        {
            AddInParameter(name, dbType, value, false);
        }

        /// <summary>
        /// 添加Input类型的参数
        /// </summary>
        /// <param name="name">参数名，建议有@符号，如果没有，框架会补偿</param>
        /// <param name="dbType">DbType</param>
        /// <param name="value">参数值</param>
        /// <param name="sensitive">参数是否敏感，如果敏感，不记入Log系统</param>
        public void AddInParameter(String name, DbType dbType, Object value, Boolean sensitive)
        {
            AddInParameter(name, dbType, value, sensitive, false);
        }

        /// <summary>
        /// 添加Input类型的参数
        /// </summary>
        /// <param name="name">参数名，建议有@符号，如果没有，框架会补偿</param>
        /// <param name="dbType">DbType</param>
        /// <param name="value">参数值</param>
        /// <param name="sensitive">参数是否敏感，如果敏感，不记入Log系统</param>
        /// <param name="isSharding">是否是Sharding字段</param>
        public void AddInParameter(String name, DbType dbType, Object value, Boolean sensitive, Boolean isSharding)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Value = value,
                Direction = ParameterDirection.Input,
                IsSensitive = sensitive,
                IsShardingColumn = isSharding
            });
        }

        public void AddOutParameter(String name, DbType dbType, Boolean sensitive = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Direction = ParameterDirection.Output,
                IsSensitive = sensitive,
            });
        }

        public void AddOutParameter(String name, DbType dbType, Int32 size, Boolean sensitive = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Direction = ParameterDirection.Output,
                Size = size,
                IsSensitive = sensitive,
            });
        }

        public void AddParameter(String name, DbType dbType, Object value, Int32 size, ParameterDirection dir, Boolean sensitive = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Value = value,
                Direction = dir,
                Size = size,
                IsSensitive = sensitive,
            });
        }

        protected override String GetKeyForItem(StatementParameter item)
        {
            return item.Name;
        }
    }
}
