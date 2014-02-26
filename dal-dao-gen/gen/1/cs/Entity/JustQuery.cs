using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [Table(Name = "")]
    public partial class JustQuery
    {
        /// <summary>
        /// 获取或设置Birth
        /// </summary>
        [Column(Name = "Birth",Length=23)]
        public DateTime Birth { get; set; }
    }
}