using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// AbacusGetTaxLog
    /// </summary>
    [Serializable]
    [Table(Name = "AbacusGetTaxLog")]
    public partial class AbacusGetTaxLogGen
    {
        /// <summary>
        /// 获取或设置LogID
        /// </summary>
        [Column(Name = "LogID",Length=10),ID,PK]
        public int LogID { get; set; }
        /// <summary>
        /// 获取或设置UID
        /// </summary>
        [Column(Name = "UID",Length=50)]
        public string UID { get; set; }
        /// <summary>
        /// 获取或设置OrderID
        /// </summary>
        [Column(Name = "OrderID",Length=10)]
        public int? OrderID { get; set; }
        /// <summary>
        /// 获取或设置PNR
        /// </summary>
        [Column(Name = "PNR",Length=10)]
        public string PNR { get; set; }
        /// <summary>
        /// 获取或设置BeginTime
        /// </summary>
        [Column(Name = "BeginTime",Length=23)]
        public DateTime BeginTime { get; set; }
        /// <summary>
        /// 获取或设置EndTime
        /// </summary>
        [Column(Name = "EndTime",Length=23)]
        public DateTime EndTime { get; set; }
        /// <summary>
        /// 获取或设置Result
        /// </summary>
        [Column(Name = "Result",Length=10)]
        public int Result { get; set; }
        /// <summary>
        /// 获取或设置Tax
        /// </summary>
        [Column(Name = "Tax",Length=10)]
        public decimal? Tax { get; set; }
        /// <summary>
        /// 获取或设置ErrDesc
        /// </summary>
        [Column(Name = "ErrDesc",Length=2000)]
        public string ErrDesc { get; set; }
        /// <summary>
        /// 获取或设置Source
        /// </summary>
        [Column(Name = "Source",Length=20)]
        public string Source { get; set; }
        /// <summary>
        /// 获取或设置InsertTime
        /// </summary>
        [Column(Name = "InsertTime",Length=23)]
        public DateTime? InsertTime { get; set; }
        /// <summary>
        /// 获取或设置PassengerType
        /// </summary>
        [Column(Name = "PassengerType",Length=5)]
        public string PassengerType { get; set; }
        /// <summary>
        /// 获取或设置OperatingAirline
        /// </summary>
        [Column(Name = "OperatingAirline",Length=50)]
        public string OperatingAirline { get; set; }
        /// <summary>
        /// 获取或设置DataChangeLastTime
        /// </summary>
        [Column(Name = "DataChangeLastTime",Length=23)]
        public DateTime? DataChangeLastTime { get; set; }
    }
}