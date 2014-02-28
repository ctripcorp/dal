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
        [Column(Name = "id",Length=10),ID,PK]
        public int Id { get; set; }
        /// <summary>
        /// 获取或设置money_all
        /// </summary>
        [Column(Name = "money_all",Length=10)]
        public decimal? Money_all { get; set; }
        /// <summary>
        /// 获取或设置bool_test
        /// </summary>
        [Column(Name = "bool_test")]
        public bool? Bool_test { get; set; }
        /// <summary>
        /// 获取或设置date_test
        /// </summary>
        [Column(Name = "date_test",Length=19)]
        public DateTime? Date_test { get; set; }
    }
}