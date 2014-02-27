using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// PrizeInfo
    /// </summary>
    [Serializable]
    [Table(Name = "PrizeInfo")]
    public partial class PrizeInfo_gen
    {
        /// <summary>
        /// 获取或设置PrizeInfoID
        /// </summary>
        [Column(Name = "PrizeInfoID",Length=10),ID,PK]
        public int PrizeInfoID { get; set; }
        /// <summary>
        /// 获取或设置PrizeActivityid
        /// </summary>
        [Column(Name = "PrizeActivityid",Length=10)]
        public int PrizeActivityid { get; set; }
        /// <summary>
        /// 获取或设置PrizeType
        /// </summary>
        [Column(Name = "PrizeType",Length=20)]
        public string PrizeType { get; set; }
        /// <summary>
        /// 获取或设置PrizeLevel
        /// </summary>
        [Column(Name = "PrizeLevel",Length=10)]
        public int PrizeLevel { get; set; }
        /// <summary>
        /// 获取或设置PrizeName
        /// </summary>
        [Column(Name = "PrizeName",Length=50)]
        public string PrizeName { get; set; }
        /// <summary>
        /// 获取或设置EffectStartTime
        /// </summary>
        [Column(Name = "EffectStartTime",Length=23)]
        public DateTime EffectStartTime { get; set; }
        /// <summary>
        /// 获取或设置EffectEndTime
        /// </summary>
        [Column(Name = "EffectEndTime",Length=23)]
        public DateTime EffectEndTime { get; set; }
        /// <summary>
        /// 获取或设置DataChange_LastTime
        /// </summary>
        [Column(Name = "DataChange_LastTime",Length=23)]
        public DateTime DataChange_LastTime { get; set; }
        /// <summary>
        /// 获取或设置PrizeCode
        /// </summary>
        [Column(Name = "PrizeCode",Length=10)]
        public string PrizeCode { get; set; }
    }
}