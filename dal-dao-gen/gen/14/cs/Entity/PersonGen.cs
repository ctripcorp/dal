using System;
using Arch.Data.Orm;

namespace DAL.Entity.DataModel
{
    /// <summary>
    /// Person
    /// </summary>
    [Serializable]
    [Table(Name = "Person")]
    public partial class PersonGen
    {
        /// <summary>
        /// 获取或设置ID
        /// </summary>
        [Column(Name = "ID",Length=10),ID,PK]
        public int ID { get; set; }
        /// <summary>
        /// 获取或设置Name
        /// </summary>
        [Column(Name = "Name",Length=50)]
        public string Name { get; set; }
        /// <summary>
        /// 获取或设置Age
        /// </summary>
        [Column(Name = "Age",Length=10)]
        public int? Age { get; set; }
        /// <summary>
        /// 获取或设置Birth
        /// </summary>
        [Column(Name = "Birth",Length=23)]
        public DateTime? Birth { get; set; }
    }
}