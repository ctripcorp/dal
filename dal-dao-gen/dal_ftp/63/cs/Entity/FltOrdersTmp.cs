using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// _FltOrdersTmp
    /// </summary>
    [Serializable]
    [Table(Name = "_FltOrdersTmp")]
    public partial class FltOrdersTmp
    {
        /// <summary>
        /// 获取或设置RecordId
        /// </summary>
        [Column(Name = "RecordId",Length=10),ID,PK]
        public int RecordId { get; set; }
        /// <summary>
        /// 获取或设置OrderId
        /// </summary>
        [Column(Name = "OrderId",Length=10)]
        public int OrderId { get; set; }
        /// <summary>
        /// 获取或设置PassengerName
        /// </summary>
        [Column(Name = "PassengerName",Length=40)]
        public string PassengerName { get; set; }
        /// <summary>
        /// 获取或设置Sequence
        /// </summary>
        [Column(Name = "Sequence",Length=5)]
        public short Sequence { get; set; }
        /// <summary>
        /// 获取或设置AccCheckId
        /// </summary>
        [Column(Name = "AccCheckId",Length=10)]
        public int AccCheckId { get; set; }
        /// <summary>
        /// 获取或设置Price
        /// </summary>
        [Column(Name = "Price",Length=19)]
        public decimal Price { get; set; }
        /// <summary>
        /// 获取或设置Tax
        /// </summary>
        [Column(Name = "Tax",Length=19)]
        public decimal Tax { get; set; }
        /// <summary>
        /// 获取或设置OilFee
        /// </summary>
        [Column(Name = "OilFee",Length=19)]
        public decimal OilFee { get; set; }
        /// <summary>
        /// 获取或设置Sendticketfee
        /// </summary>
        [Column(Name = "Sendticketfee",Length=19)]
        public decimal Sendticketfee { get; set; }
        /// <summary>
        /// 获取或设置Insurancefee
        /// </summary>
        [Column(Name = "Insurancefee",Length=19)]
        public decimal Insurancefee { get; set; }
        /// <summary>
        /// 获取或设置ServiceFee
        /// </summary>
        [Column(Name = "ServiceFee",Length=19)]
        public decimal ServiceFee { get; set; }
        /// <summary>
        /// 获取或设置Refund
        /// </summary>
        [Column(Name = "Refund",Length=19)]
        public decimal Refund { get; set; }
        /// <summary>
        /// 获取或设置delAdjustAmount
        /// </summary>
        [Column(Name = "delAdjustAmount",Length=19)]
        public decimal DelAdjustAmount { get; set; }
        /// <summary>
        /// 获取或设置AdjustedAmount
        /// </summary>
        [Column(Name = "AdjustedAmount",Length=19)]
        public decimal AdjustedAmount { get; set; }
        /// <summary>
        /// 获取或设置OrderStatus
        /// </summary>
        [Column(Name = "OrderStatus",Length=1)]
        public char OrderStatus { get; set; }
        /// <summary>
        /// 获取或设置Remark
        /// </summary>
        [Column(Name = "Remark",Length=200)]
        public string Remark { get; set; }
        /// <summary>
        /// 获取或设置CreateTime
        /// </summary>
        [Column(Name = "CreateTime",Length=23)]
        public DateTime CreateTime { get; set; }
        /// <summary>
        /// 获取或设置ConfirmTime
        /// </summary>
        [Column(Name = "ConfirmTime",Length=23)]
        public DateTime? ConfirmTime { get; set; }
        /// <summary>
        /// 获取或设置DailyConfirmFlag
        /// </summary>
        [Column(Name = "DailyConfirmFlag",Length=1)]
        public char? DailyConfirmFlag { get; set; }
        /// <summary>
        /// 获取或设置DealID
        /// </summary>
        [Column(Name = "DealID",Length=10)]
        public int? DealID { get; set; }
        /// <summary>
        /// 获取或设置Cost
        /// </summary>
        [Column(Name = "Cost",Length=19)]
        public decimal? Cost { get; set; }
        /// <summary>
        /// 获取或设置DataChangeLastTime
        /// </summary>
        [Column(Name = "DataChangeLastTime",Length=23)]
        public DateTime? DataChangeLastTime { get; set; }
    }
}