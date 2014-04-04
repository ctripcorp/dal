using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// AbacusPara
    /// </summary>
    [Serializable]
    [Table(Name = "AbacusPara")]
    public partial class AbacusParaGen
    {
        /// <summary>
        /// 获取或设置ParaID
        /// </summary>
        [Column(Name = "ParaID",Length=10),PK]
        public int ParaID { get; set; }
        /// <summary>
        /// 获取或设置ParaTypeID
        /// </summary>
        [Column(Name = "ParaTypeID",Length=5)]
        public short? ParaTypeID { get; set; }
        /// <summary>
        /// 获取或设置ParaName
        /// </summary>
        [Column(Name = "ParaName",Length=50)]
        public string ParaName { get; set; }
        /// <summary>
        /// 获取或设置ParaValue
        /// </summary>
        [Column(Name = "ParaValue",Length=1000)]
        public string ParaValue { get; set; }
        /// <summary>
        /// 获取或设置Description
        /// </summary>
        [Column(Name = "Description",Length=50)]
        public string Description { get; set; }
        /// <summary>
        /// 获取或设置AbacusWSID
        /// </summary>
        [Column(Name = "AbacusWSID",Length=5)]
        public short? AbacusWSID { get; set; }
        /// <summary>
        /// 获取或设置DataChangeLastTime
        /// </summary>
        [Column(Name = "DataChangeLastTime",Length=23)]
        public DateTime? DataChangeLastTime { get; set; }
    }
}