using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.uat.Entity.DataModel
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
        [Column(Name = "PeopleID")]
        public ulong PeopleID { get; set; }
        /// <summary>
        /// 获取或设置Name
        /// </summary>
        [Column(Name = "Name",Length=50)]
        public string Name { get; set; }
        /// <summary>
        /// 获取或设置CityID
        /// </summary>
        [Column(Name = "CityID")]
        public uint CityID { get; set; }
        /// <summary>
        /// 获取或设置ProvinceID
        /// </summary>
        [Column(Name = "ProvinceID")]
        public uint ProvinceID { get; set; }
        /// <summary>
        /// 获取或设置CountryID
        /// </summary>
        [Column(Name = "CountryID")]
        public uint CountryID { get; set; }
        /// <summary>
        /// 获取或设置CityName
        /// </summary>
        [Column(Name = "CityName")]
        public char CityName { get; set; }
    }
}