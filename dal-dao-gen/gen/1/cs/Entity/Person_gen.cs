using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// Person
    /// </summary>
    [Serializable]
    [Table(Name = "Person")]
    public partial class Person_gen
    {
        /// <summary>
        /// 获取或设置ID
        /// </summary>
        [Column(Name = "ID"),ID,PK]
        public int ID { get; set; }
        /// <summary>
        /// 获取或设置Name
        /// </summary>
        [Column(Name = "Name",Length=50)]
        public string Name { get; set; }
        /// <summary>
        /// 获取或设置Age
        /// </summary>
        [Column(Name = "Age")]
        public int? Age { get; set; }
        /// <summary>
        /// 获取或设置Birth
        /// </summary>
        [Column(Name = "Birth")]
        public DateTime? Birth { get; set; }
    }
}