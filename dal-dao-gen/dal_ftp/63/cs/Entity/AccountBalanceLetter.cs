using System;
using Arch.Data.Orm;

namespace com.ctrip.dal.test.test4.Entity.DataModel
{
    /// <summary>
    /// AccountBalanceLetter
    /// </summary>
    [Serializable]
    [Table(Name = "AccountBalanceLetter")]
    public partial class AccountBalanceLetter
    {
        /// <summary>
        /// 获取或设置RecordId
        /// </summary>
        [Column(Name = "RecordId",Length=10),ID,PK]
        public int RecordId { get; set; }
        /// <summary>
        /// 获取或设置AccBalanceId
        /// </summary>
        [Column(Name = "AccBalanceId",Length=10)]
        public int? AccBalanceId { get; set; }
        /// <summary>
        /// 获取或设置FileName
        /// </summary>
        [Column(Name = "FileName",Length=500)]
        public string FileName { get; set; }
        /// <summary>
        /// 获取或设置UploadPerson
        /// </summary>
        [Column(Name = "UploadPerson",Length=500)]
        public string UploadPerson { get; set; }
        /// <summary>
        /// 获取或设置DescribeInfo
        /// </summary>
        [Column(Name = "DescribeInfo",Length=50)]
        public string DescribeInfo { get; set; }
        /// <summary>
        /// 获取或设置CreateTime
        /// </summary>
        [Column(Name = "CreateTime",Length=23)]
        public DateTime? CreateTime { get; set; }
        /// <summary>
        /// 获取或设置DataChangeLastTime
        /// </summary>
        [Column(Name = "DataChangeLastTime",Length=23)]
        public DateTime? DataChangeLastTime { get; set; }
    }
}