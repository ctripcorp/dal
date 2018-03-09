using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    /// <summary>
    /// 数据库集配置元素
    /// </summary>
    public sealed class DatabaseSetElement : ConfigurationElement
    {
        #region private fields

        /// <summary>
        /// 名称
        /// </summary>
        private const String c_NameProperty = "name";

        /// <summary>
        /// 提供者名称
        /// </summary>
        private const String c_ProviderProperty = "provider";

        /// <summary>
        /// 分片策略
        /// </summary>
        private const String c_ShardingStrategyProperty = "shardingStrategy";

        #endregion

        /// <summary>
        /// 名称
        /// </summary>
        [ConfigurationProperty("name", IsKey = true)]
        public String Name
        {
            get { return (String)base[c_NameProperty]; }
            set { base[c_NameProperty] = value; }
        }

        /// <summary>
        /// 提供者名称
        /// </summary>
        [ConfigurationProperty("provider", IsKey = true)]
        public String Provider
        {
            get { return (String)base[c_ProviderProperty]; }
            set { base[c_ProviderProperty] = value; }
        }

        /// <summary>
        /// 数据库sharding策略配置项
        /// </summary>
        [ConfigurationProperty("", IsDefaultCollection = true)]
        public DatabaseElementCollection Databases
        {
            get { return (DatabaseElementCollection)this[String.Empty]; }
        }

        /// <summary>
        /// shardStrategyClass属性 
        /// </summary>
        [ConfigurationProperty("shardingStrategy", IsKey = true)]
        public String ShardingStrategy
        {
            get { return (String)base[c_ShardingStrategyProperty]; }
            set { base[c_ShardingStrategyProperty] = value; }
        }

    }
}
