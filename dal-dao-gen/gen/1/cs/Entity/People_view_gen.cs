using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// People_view
    /// </summary>
    [Serializable]
    [Table(Name = "People_view")]
    public partial class People_view_gen
    {
        /// <summary>
        /// 获取或设置PeopleID
        /// </summary>
        [Column(Name = "PeopleID",Length=19)]
        public long PeopleID { get; set; }
        /// <summary>
        /// 获取或设置Name
        /// </summary>
        [Column(Name = "Name",Length=50)]
        public string Name { get; set; }
        /// <summary>
        /// 获取或设置CityID
        /// </summary>
        [Column(Name = "CityID",Length=10)]
        public int CityID { get; set; }
        /// <summary>
        /// 获取或设置ProvinceID
        /// </summary>
        [Column(Name = "ProvinceID",Length=10)]
        public int ProvinceID { get; set; }
        /// <summary>
        /// 获取或设置CountryID
        /// </summary>
        [Column(Name = "CountryID",Length=10)]
        public int CountryID { get; set; }
        /// <summary>
        /// 获取或设置CityName
        /// </summary>
        [Column(Name = "CityName",Length=100)]
        public char CityName { get; set; }
    }
}