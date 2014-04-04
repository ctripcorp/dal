using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// test2
    /// </summary>
    [Serializable]
    [Table(Name = "test2")]
    public partial class Test2
    {
        /// <summary>
        /// 获取或设置AccBalanceID
        /// </summary>
        [Column(Name = "AccBalanceID",Length=10),ID]
        public int AccBalanceID { get; set; }
        /// <summary>
        /// 获取或设置BatchNo
        /// </summary>
        [Column(Name = "BatchNo",Length=60)]
        public string BatchNo { get; set; }
        /// <summary>
        /// 获取或设置AccountID
        /// </summary>
        [Column(Name = "AccountID",Length=10)]
        public int AccountID { get; set; }
        /// <summary>
        /// 获取或设置CreateTime
        /// </summary>
        [Column(Name = "CreateTime",Length=23)]
        public DateTime CreateTime { get; set; }
        /// <summary>
        /// 获取或设置StartDate
        /// </summary>
        [Column(Name = "StartDate",Length=8)]
        public string StartDate { get; set; }
        /// <summary>
        /// 获取或设置EndDate
        /// </summary>
        [Column(Name = "EndDate",Length=8)]
        public string EndDate { get; set; }
        /// <summary>
        /// 获取或设置recReturn
        /// </summary>
        [Column(Name = "recReturn",Length=19)]
        public decimal RecReturn { get; set; }
        /// <summary>
        /// 获取或设置ReportCompletionDay
        /// </summary>
        [Column(Name = "ReportCompletionDay",Length=23)]
        public DateTime? ReportCompletionDay { get; set; }
        /// <summary>
        /// 获取或设置ContractFirmDay
        /// </summary>
        [Column(Name = "ContractFirmDay",Length=23)]
        public DateTime? ContractFirmDay { get; set; }
        /// <summary>
        /// 获取或设置ContractDay
        /// </summary>
        [Column(Name = "ContractDay",Length=23)]
        public DateTime? ContractDay { get; set; }
        /// <summary>
        /// 获取或设置ReConfirmDate
        /// </summary>
        [Column(Name = "ReConfirmDate",Length=23)]
        public DateTime? ReConfirmDate { get; set; }
        /// <summary>
        /// 获取或设置ContractDate
        /// </summary>
        [Column(Name = "ContractDate",Length=23)]
        public DateTime? ContractDate { get; set; }
        /// <summary>
        /// 获取或设置LastAuditDate
        /// </summary>
        [Column(Name = "LastAuditDate",Length=23)]
        public DateTime? LastAuditDate { get; set; }
        /// <summary>
        /// 获取或设置SendRptDate
        /// </summary>
        [Column(Name = "SendRptDate",Length=23)]
        public DateTime? SendRptDate { get; set; }
        /// <summary>
        /// 获取或设置AuditDate
        /// </summary>
        [Column(Name = "AuditDate",Length=23)]
        public DateTime? AuditDate { get; set; }
        /// <summary>
        /// 获取或设置ReceiveDate
        /// </summary>
        [Column(Name = "ReceiveDate",Length=23)]
        public DateTime? ReceiveDate { get; set; }
        /// <summary>
        /// 获取或设置RptDate
        /// </summary>
        [Column(Name = "RptDate",Length=23)]
        public DateTime? RptDate { get; set; }
        /// <summary>
        /// 获取或设置ConfirmDate
        /// </summary>
        [Column(Name = "ConfirmDate",Length=23)]
        public DateTime? ConfirmDate { get; set; }
        /// <summary>
        /// 获取或设置BalDate
        /// </summary>
        [Column(Name = "BalDate",Length=23)]
        public DateTime? BalDate { get; set; }
        /// <summary>
        /// 获取或设置InvoiceDate
        /// </summary>
        [Column(Name = "InvoiceDate",Length=23)]
        public DateTime? InvoiceDate { get; set; }
        /// <summary>
        /// 获取或设置ConfirmFlag
        /// </summary>
        [Column(Name = "ConfirmFlag",Length=1)]
        public char? ConfirmFlag { get; set; }
        /// <summary>
        /// 获取或设置LastAuditFlag
        /// </summary>
        [Column(Name = "LastAuditFlag",Length=1)]
        public char? LastAuditFlag { get; set; }
        /// <summary>
        /// 获取或设置SettleFlag
        /// </summary>
        [Column(Name = "SettleFlag",Length=1)]
        public char? SettleFlag { get; set; }
    }
}