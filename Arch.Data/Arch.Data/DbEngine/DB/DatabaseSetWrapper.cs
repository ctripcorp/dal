using Arch.Data.Common.Enums;
using Arch.Data.DbEngine.Sharding;
using System;
using System.Collections.Generic;

namespace Arch.Data.DbEngine.DB
{
    public class DatabaseSetWrapper
    {
        #region private fields

        /// <summary>
        /// 数据集名称
        /// </summary>
        public String Name { get; set; }

        /// <summary>
        /// 数据集提供者类型
        /// </summary>
        //public string ProviderType { get; set; }
        public DatabaseProviderType ProviderType { get; set; }

        /// <summary>
        /// 是否支持读写分离
        /// </summary>
        public Boolean EnableReadWriteSpliding { get; set; }

        /// <summary>
        /// 从数据库分片权重
        /// </summary>
        private Dictionary<String, Int32> m_TotalRatios;

        /// <summary>
        /// 数据库列表
        /// </summary>
        private List<DatabaseWrapper> m_DatabaseWrappers;

        /// <summary>
        /// 分片列表
        /// </summary>
        public ISet<String> AllShards = new HashSet<String>();

        /// <summary>
        /// Sharding策略
        /// </summary>
        public IShardingStrategy ShardingStrategy { get; set; }

        #endregion

        /// <summary>
        /// 从数据库分片权重
        /// </summary>
        public Dictionary<String, Int32> TotalRatios
        {
            get
            {
                if (m_TotalRatios == null)
                    m_TotalRatios = new Dictionary<String, Int32>();
                return m_TotalRatios;
            }
        }

        /// <summary>
        /// 数据库列表
        /// </summary>
        public List<DatabaseWrapper> DatabaseWrappers
        {
            get
            {
                if (m_DatabaseWrappers == null)
                    m_DatabaseWrappers = new List<DatabaseWrapper>();
                return m_DatabaseWrappers;
            }
        }

    }
}
