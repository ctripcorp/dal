using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [Table(Name = "")]
    public partial class Test
    {
        /// <summary>
        /// 获取或设置ID
        /// </summary>
        [Column(Name = "ID",Length=11)]
        public int ID { get; set; }
        /// <summary>
        /// 获取或设置Name
        /// </summary>
        [Column(Name = "Name",Length=50)]
        public string Name { get; set; }
        /// <summary>
        /// 获取或设置Age
        /// </summary>
        [Column(Name = "Age",Length=11)]
        public int Age { get; set; }
        /// <summary>
        /// 获取或设置Birth
        /// </summary>
        [Column(Name = "Birth",Length=23)]
        public DateTime Birth { get; set; }
    }
}