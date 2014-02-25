using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Dao
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [Table(Name = "")]
    public partial class Test
    {
        /// <summary>
        /// ��ȡ������ID
        /// </summary>
        [Column(Name = "ID",Length=11)]
        public uint ID { get; set; }
        /// <summary>
        /// ��ȡ������Name
        /// </summary>
        [Column(Name = "Name",Length=50)]
        public string Name { get; set; }
        /// <summary>
        /// ��ȡ������Age
        /// </summary>
        [Column(Name = "Age",Length=11)]
        public uint Age { get; set; }
        /// <summary>
        /// ��ȡ������Birth
        /// </summary>
        [Column(Name = "Birth",Length=23)]
        public DateTime Birth { get; set; }
    }
}