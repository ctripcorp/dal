using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Dao
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [Table(Name = "")]
    public partial class JustQuery
    {
        /// <summary>
        /// ��ȡ������Birth
        /// </summary>
        [Column(Name = "Birth",Length=23)]
        public DateTime Birth { get; set; }
    }
}