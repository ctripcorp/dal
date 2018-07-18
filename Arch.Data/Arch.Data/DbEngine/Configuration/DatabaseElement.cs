using Arch.Data.Common.Enums;
using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    /// <summary>
    /// 物理数据库链接配置元素
    /// </summary>
    public sealed class DatabaseElement : ConfigurationElement
    {
        #region private fields

        /// <summary>
        /// 名称
        /// </summary>
        private const String c_NameProperty = "name";

        /// <summary>
        /// 数据库主从类型
        /// </summary>
        private const String c_DatabaseTypeProperty = "databaseType";

        /// <summary>
        /// 分片标识
        /// </summary>
        private const String c_ShardingProperty = "sharding";

        /// <summary>
        /// 调用权重
        /// </summary>
        private const String c_RatioProperty = "ratio";

        /// <summary>
        /// 链接字符串名称
        /// </summary>
        private const String c_ConnectionStringProperty = "connectionString";

        /// <summary>
        /// 连续型sharding初始值
        /// </summary>
        private const String c_SequenceStart = "start";

        /// <summary>
        /// 连续型sharding结束值
        /// </summary>
        private const String c_SequenceEnd = "end";

        #endregion

        #region public properties

        /// <summary>
        /// 名称,关键字
        /// </summary>
        [ConfigurationProperty(c_NameProperty, IsRequired = true)]
        public String Name
        {
            get { return (String)this[c_NameProperty]; }
            set { this[c_NameProperty] = value; }
        }

        /// <summary>
        /// 从库访问权重
        /// </summary>
        [ConfigurationProperty(c_RatioProperty)]
        public Int32 Ratio
        {
            get { return (Int32)this[c_RatioProperty]; }
            set { this[c_RatioProperty] = value; }
        }

        /// <summary>
        /// 数据库主从类型
        /// </summary>
        [ConfigurationProperty(c_DatabaseTypeProperty, DefaultValue = DatabaseType.Master)]
        public DatabaseType DatabaseType
        {
            get { return (DatabaseType)this[c_DatabaseTypeProperty]; }
            set { this[c_DatabaseTypeProperty] = value; }
        }

        /// <summary>
        /// 分片标识
        /// </summary>
        [ConfigurationProperty(c_ShardingProperty, DefaultValue = "")]
        public String Sharding
        {
            get { return (String)this[c_ShardingProperty]; }
            set { this[c_ShardingProperty] = value; }
        }

        /// <summary>
        /// 链接字符串名称
        /// </summary>
        [ConfigurationProperty(c_ConnectionStringProperty, IsRequired = true)]
        public String ConnectionString
        {
            get { return (String)this[c_ConnectionStringProperty]; }
            set { this[c_ConnectionStringProperty] = value; }
        }

        /// <summary>
        /// 连续型
        /// </summary>
        [ConfigurationProperty(c_SequenceStart)]
        public String Start
        {
            get { return (String)this[c_SequenceStart]; }
            set { this[c_SequenceStart] = value; }
        }

        /// <summary>
        /// 连续型
        /// </summary>
        [ConfigurationProperty(c_SequenceEnd)]
        public String End
        {
            get { return (String)this[c_SequenceEnd]; }
            set { this[c_SequenceEnd] = value; }
        }

        #endregion
    }
}
