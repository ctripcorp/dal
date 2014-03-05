using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// SDP_Version
    /// </summary>
    [Serializable]
    [Table(Name = "SDP_Version")]
    public partial class SDP_Version_gen
    {
        /// <summary>
        /// 获取或设置ID
        /// </summary>
        [Column(Name = "ID",Length=19),ID,PK]
        public long ID { get; set; }
        /// <summary>
        /// 获取或设置TableName
        /// </summary>
        [Column(Name = "TableName",Length=50)]
        public string TableName { get; set; }
        /// <summary>
        /// 获取或设置Version
        /// </summary>
        [Column(Name = "Version",Length=23)]
        public DateTime Version { get; set; }
        /// <summary>
        /// 获取或设置Flag
        /// </summary>
        [Column(Name = "Flag",Length=10)]
        public int Flag { get; set; }
    }
}