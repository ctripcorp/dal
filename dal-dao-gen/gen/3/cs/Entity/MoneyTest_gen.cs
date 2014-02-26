using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.uat.Entity.DataModel
{
    /// <summary>
    /// MoneyTest
    /// </summary>
    [Serializable]
    [Table(Name = "MoneyTest")]
    public partial class MoneyTest_gen
    {
        /// <summary>
        /// 获取或设置id
        /// </summary>
        [Column(Name = "id"),ID,PK]
        public uint Id { get; set; }
        /// <summary>
        /// 获取或设置money_all
        /// </summary>
        [Column(Name = "money_all")]
        public decimal Money_all { get; set; }
    }
}