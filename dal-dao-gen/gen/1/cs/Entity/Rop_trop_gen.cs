using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// rop_trop
    /// </summary>
    [Serializable]
    [Table(Name = "rop_trop")]
    public partial class Rop_trop_gen
    {
        /// <summary>
        /// 获取或设置id
        /// </summary>
        [Column(Name = "id",Length=10),ID,PK]
        public int Id { get; set; }
        /// <summary>
        /// 获取或设置rop_id
        /// </summary>
        [Column(Name = "rop_id",Length=10)]
        public int Rop_id { get; set; }
        /// <summary>
        /// 获取或设置createdtime
        /// </summary>
        [Column(Name = "createdtime",Length=19)]
        public DateTime Createdtime { get; set; }
        /// <summary>
        /// 获取或设置updatedtime
        /// </summary>
        [Column(Name = "updatedtime",Length=19)]
        public DateTime Updatedtime { get; set; }
        /// <summary>
        /// 获取或设置created_by
        /// </summary>
        [Column(Name = "created_by",Length=100)]
        public string Created_by { get; set; }
        /// <summary>
        /// 获取或设置modified_by
        /// </summary>
        [Column(Name = "modified_by",Length=100)]
        public string Modified_by { get; set; }
        /// <summary>
        /// 获取或设置appinfo
        /// </summary>
        [Column(Name = "appinfo",Length=32)]
        public string Appinfo { get; set; }
    }
}