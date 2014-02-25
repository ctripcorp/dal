using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Dao
{
    /// <summary>
    /// Person
    /// </summary>
    [Serializable]
    [Table(Name = "Person")]
    public partial class Person_gen
    {
        /// <summary>
        /// ��ȡ������ID
        /// </summary>
        [Column(Name = "ID"),ID,PK]
        public uint ID { get; set; }
        /// <summary>
        /// ��ȡ������Name
        /// </summary>
        [Column(Name = "Name",Length=50)]
        public string Name { get; set; }
        /// <summary>
        /// ��ȡ������Age
        /// </summary>
        [Column(Name = "Age")]
        public uint Age { get; set; }
        /// <summary>
        /// ��ȡ������Birth
        /// </summary>
        [Column(Name = "Birth")]
        public DateTime? Birth { get; set; }
    }
}