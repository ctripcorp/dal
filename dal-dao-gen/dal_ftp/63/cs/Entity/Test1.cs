using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// test1
    /// </summary>
    [Serializable]
    [Table(Name = "test1")]
    public partial class Test1
    {
        /// <summary>
        /// 获取或设置HtlOrderDetailId
        /// </summary>
        [Column(Name = "HtlOrderDetailId",Length=10),ID]
        public int HtlOrderDetailId { get; set; }
        /// <summary>
        /// 获取或设置OrderId
        /// </summary>
        [Column(Name = "OrderId",Length=10)]
        public int? OrderId { get; set; }
        /// <summary>
        /// 获取或设置OrderType
        /// </summary>
        [Column(Name = "OrderType",Length=1)]
        public char? OrderType { get; set; }
        /// <summary>
        /// 获取或设置Amount
        /// </summary>
        [Column(Name = "Amount",Length=19)]
        public decimal? Amount { get; set; }
        /// <summary>
        /// 获取或设置ServiceFee
        /// </summary>
        [Column(Name = "ServiceFee",Length=19)]
        public decimal? ServiceFee { get; set; }
        /// <summary>
        /// 获取或设置Rebate
        /// </summary>
        [Column(Name = "Rebate",Length=19)]
        public decimal? Rebate { get; set; }
        /// <summary>
        /// 获取或设置IsInbatch
        /// </summary>
        [Column(Name = "IsInbatch",Length=1)]
        public char? IsInbatch { get; set; }
        /// <summary>
        /// 获取或设置AccCheckId
        /// </summary>
        [Column(Name = "AccCheckId",Length=10)]
        public int? AccCheckId { get; set; }
        /// <summary>
        /// 获取或设置CreatTime
        /// </summary>
        [Column(Name = "CreatTime",Length=23)]
        public DateTime? CreatTime { get; set; }
        /// <summary>
        /// 获取或设置InbatchTime
        /// </summary>
        [Column(Name = "InbatchTime",Length=23)]
        public DateTime? InbatchTime { get; set; }
        /// <summary>
        /// 获取或设置LastModifyTime
        /// </summary>
        [Column(Name = "LastModifyTime",Length=23)]
        public DateTime? LastModifyTime { get; set; }
        /// <summary>
        /// 获取或设置AccountId
        /// </summary>
        [Column(Name = "AccountId",Length=10)]
        public int? AccountId { get; set; }
        /// <summary>
        /// 获取或设置SubAccountID
        /// </summary>
        [Column(Name = "SubAccountID",Length=10)]
        public int? SubAccountID { get; set; }
        /// <summary>
        /// 获取或设置Rid
        /// </summary>
        [Column(Name = "Rid",Length=10)]
        public int? Rid { get; set; }
        /// <summary>
        /// 获取或设置RCTime
        /// </summary>
        [Column(Name = "RCTime",Length=23)]
        public DateTime? RCTime { get; set; }
        /// <summary>
        /// 获取或设置RCQuantity
        /// </summary>
        [Column(Name = "RCQuantity",Length=10)]
        public int? RCQuantity { get; set; }
    }
}