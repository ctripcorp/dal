using Arch.Data.Common.Enums;
using Arch.Data.DbEngine.Providers;
using System;

namespace Arch.Data.DbEngine.DB
{
    public class DatabaseWrapper
    {
        /// <summary>
        /// 物理数据库名称
        /// </summary>
        public String Name { get; set; }

        /// <summary>
        /// 数据库主从类型
        /// </summary>
        public DatabaseType DatabaseType { get; set; }

        /// <summary>
        /// 分片标识
        /// </summary>
        public String Sharding { get; set; }

        /// <summary>
        /// 读权重
        /// </summary>
        public Int32 Ratio { get; set; }

        /// <summary>
        /// 读权重起始值
        /// </summary>
        public Int32 RatioStart { get; set; }

        /// <summary>
        /// 读权重结束值
        /// </summary>
        public Int32 RatioEnd { get; set; }

        /// <summary>
        /// 连接字符串
        /// </summary>
        public String ConnectionString { get; set; }

        /// <summary>
        /// Driver Provider类型
        /// </summary>
        public IDatabaseProvider DatabaseProvider { get; set; }

        /// <summary>
        /// 数据库
        /// </summary>
        public Database Database { get; set; }

    }
}
