using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// sysdiagrams
    /// </summary>
    [Serializable]
    [Table(Name = "sysdiagrams")]
    public partial class Sysdiagrams_gen
    {
        /// <summary>
        /// 获取或设置name
        /// </summary>
        [Column(Name = "name",Length=128)]
        public string Name { get; set; }
        /// <summary>
        /// 获取或设置principal_id
        /// </summary>
        [Column(Name = "principal_id",Length=10)]
        public int Principal_id { get; set; }
        /// <summary>
        /// 获取或设置diagram_id
        /// </summary>
        [Column(Name = "diagram_id",Length=10),ID,PK]
        public int Diagram_id { get; set; }
        /// <summary>
        /// 获取或设置version
        /// </summary>
        [Column(Name = "version",Length=10)]
        public int? Version { get; set; }
        /// <summary>
        /// 获取或设置definition
        /// </summary>
        [Column(Name = "definition",Length=2147483647)]
        public ${column.getType()} Definition { get; set; }
    }
}