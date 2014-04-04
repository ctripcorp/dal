using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// test
    /// </summary>
    [Serializable]
    [Table(Name = "test")]
    public partial class Test
    {
        /// <summary>
        /// 获取或设置id
        /// </summary>
        [Column(Name = "id",Length=10),ID]
        public int Id { get; set; }
        /// <summary>
        /// 获取或设置status
        /// </summary>
        [Column(Name = "status",Length=10)]
        public char? Status { get; set; }
        /// <summary>
        /// 获取或设置number
        /// </summary>
        [Column(Name = "number",Length=10)]
        public int? Number { get; set; }
        /// <summary>
        /// 获取或设置AccBalanceID
        /// </summary>
        [Column(Name = "AccBalanceID",Length=10)]
        public int? AccBalanceID { get; set; }
        /// <summary>
        /// 获取或设置AccountID
        /// </summary>
        [Column(Name = "AccountID",Length=10)]
        public int AccountID { get; set; }
        /// <summary>
        /// 获取或设置StartDate
        /// </summary>
        [Column(Name = "StartDate",Length=8)]
        public char? StartDate { get; set; }
        /// <summary>
        /// 获取或设置AccountName
        /// </summary>
        [Column(Name = "AccountName",Length=100)]
        public string AccountName { get; set; }
        /// <summary>
        /// 获取或设置CompanyName
        /// </summary>
        [Column(Name = "CompanyName",Length=100)]
        public string CompanyName { get; set; }
        /// <summary>
        /// 获取或设置accountType
        /// </summary>
        [Column(Name = "accountType",Length=1)]
        public char AccountType { get; set; }
        /// <summary>
        /// 获取或设置accountTypeName
        /// </summary>
        [Column(Name = "accountTypeName",Length=8)]
        public string AccountTypeName { get; set; }
        /// <summary>
        /// 获取或设置CheckAccType
        /// </summary>
        [Column(Name = "CheckAccType",Length=10)]
        public int CheckAccType { get; set; }
        /// <summary>
        /// 获取或设置CheckAccTypeName
        /// </summary>
        [Column(Name = "CheckAccTypeName",Length=10)]
        public string CheckAccTypeName { get; set; }
        /// <summary>
        /// 获取或设置fltconMoney
        /// </summary>
        [Column(Name = "fltconMoney",Length=19)]
        public decimal FltconMoney { get; set; }
        /// <summary>
        /// 获取或设置HtlXconMoney
        /// </summary>
        [Column(Name = "HtlXconMoney",Length=19)]
        public decimal HtlXconMoney { get; set; }
        /// <summary>
        /// 获取或设置HtlHconMoney
        /// </summary>
        [Column(Name = "HtlHconMoney",Length=19)]
        public decimal HtlHconMoney { get; set; }
        /// <summary>
        /// 获取或设置settleConsume
        /// </summary>
        [Column(Name = "settleConsume",Length=1)]
        public char? SettleConsume { get; set; }
        /// <summary>
        /// 获取或设置settleService
        /// </summary>
        [Column(Name = "settleService",Length=1)]
        public char? SettleService { get; set; }
        /// <summary>
        /// 获取或设置settleReturn
        /// </summary>
        [Column(Name = "settleReturn",Length=1)]
        public char? SettleReturn { get; set; }
        /// <summary>
        /// 获取或设置conappointed
        /// </summary>
        [Column(Name = "conappointed",Length=60)]
        public string Conappointed { get; set; }
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
        /// 获取或设置InvoiceDate
        /// </summary>
        [Column(Name = "InvoiceDate",Length=23)]
        public DateTime? InvoiceDate { get; set; }
        /// <summary>
        /// 获取或设置ReportCompletionDay
        /// </summary>
        [Column(Name = "ReportCompletionDay",Length=23)]
        public DateTime? ReportCompletionDay { get; set; }
        /// <summary>
        /// 获取或设置RptDate
        /// </summary>
        [Column(Name = "RptDate",Length=23)]
        public DateTime? RptDate { get; set; }
        /// <summary>
        /// 获取或设置AuditDate
        /// </summary>
        [Column(Name = "AuditDate",Length=23)]
        public DateTime? AuditDate { get; set; }
        /// <summary>
        /// 获取或设置SendRptDate
        /// </summary>
        [Column(Name = "SendRptDate",Length=23)]
        public DateTime? SendRptDate { get; set; }
        /// <summary>
        /// 获取或设置ConfirmDate
        /// </summary>
        [Column(Name = "ConfirmDate",Length=23)]
        public DateTime? ConfirmDate { get; set; }
        /// <summary>
        /// 获取或设置LastAuditDate
        /// </summary>
        [Column(Name = "LastAuditDate",Length=23)]
        public DateTime? LastAuditDate { get; set; }
        /// <summary>
        /// 获取或设置RptDateOper
        /// </summary>
        [Column(Name = "RptDateOper",Length=50)]
        public string RptDateOper { get; set; }
        /// <summary>
        /// 获取或设置ConfirmDateOper
        /// </summary>
        [Column(Name = "ConfirmDateOper",Length=50)]
        public string ConfirmDateOper { get; set; }
        /// <summary>
        /// 获取或设置IsDailyAudit
        /// </summary>
        [Column(Name = "IsDailyAudit",Length=1)]
        public char? IsDailyAudit { get; set; }
        /// <summary>
        /// 获取或设置ReConfirmflag
        /// </summary>
        [Column(Name = "ReConfirmflag",Length=1)]
        public char? ReConfirmflag { get; set; }
    }
}