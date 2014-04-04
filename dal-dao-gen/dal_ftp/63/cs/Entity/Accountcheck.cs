using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// _accountcheck
    /// </summary>
    [Serializable]
    [Table(Name = "_accountcheck")]
    public partial class Accountcheck
    {
        /// <summary>
        /// 获取或设置AccCheckID
        /// </summary>
        [Column(Name = "AccCheckID",Length=10),ID]
        public int AccCheckID { get; set; }
        /// <summary>
        /// 获取或设置CorpID
        /// </summary>
        [Column(Name = "CorpID",Length=20)]
        public string CorpID { get; set; }
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
        /// 获取或设置SubAccountID
        /// </summary>
        [Column(Name = "SubAccountID",Length=10)]
        public int? SubAccountID { get; set; }
        /// <summary>
        /// 获取或设置BatchStatus
        /// </summary>
        [Column(Name = "BatchStatus",Length=1)]
        public char BatchStatus { get; set; }
        /// <summary>
        /// 获取或设置AccBalanceID
        /// </summary>
        [Column(Name = "AccBalanceID",Length=10)]
        public int? AccBalanceID { get; set; }
        /// <summary>
        /// 获取或设置AccountType
        /// </summary>
        [Column(Name = "AccountType",Length=1)]
        public char AccountType { get; set; }
        /// <summary>
        /// 获取或设置CheckAccType
        /// </summary>
        [Column(Name = "CheckAccType",Length=10)]
        public int CheckAccType { get; set; }
        /// <summary>
        /// 获取或设置Operator
        /// </summary>
        [Column(Name = "Operator",Length=20)]
        public string Operator { get; set; }
        /// <summary>
        /// 获取或设置ModifyTime
        /// </summary>
        [Column(Name = "ModifyTime",Length=23)]
        public DateTime ModifyTime { get; set; }
        /// <summary>
        /// 获取或设置StartDate
        /// </summary>
        [Column(Name = "StartDate",Length=8)]
        public char? StartDate { get; set; }
        /// <summary>
        /// 获取或设置EndDate
        /// </summary>
        [Column(Name = "EndDate",Length=8)]
        public char? EndDate { get; set; }
        /// <summary>
        /// 获取或设置FltconMoney
        /// </summary>
        [Column(Name = "FltconMoney",Length=19)]
        public decimal? FltconMoney { get; set; }
        /// <summary>
        /// 获取或设置HtlHconMoney
        /// </summary>
        [Column(Name = "HtlHconMoney",Length=19)]
        public decimal? HtlHconMoney { get; set; }
        /// <summary>
        /// 获取或设置HtlXconMoney
        /// </summary>
        [Column(Name = "HtlXconMoney",Length=19)]
        public decimal? HtlXconMoney { get; set; }
        /// <summary>
        /// 获取或设置limited
        /// </summary>
        [Column(Name = "limited",Length=19)]
        public decimal? Limited { get; set; }
        /// <summary>
        /// 获取或设置LimitedTemp
        /// </summary>
        [Column(Name = "LimitedTemp",Length=19)]
        public decimal? LimitedTemp { get; set; }
        /// <summary>
        /// 获取或设置DataChangeLastTime
        /// </summary>
        [Column(Name = "DataChangeLastTime",Length=23)]
        public DateTime? DataChangeLastTime { get; set; }
    }
}