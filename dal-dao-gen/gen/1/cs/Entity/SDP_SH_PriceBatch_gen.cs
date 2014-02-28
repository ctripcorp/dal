using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
    /// <summary>
    /// SDP_SH_PriceBatch
    /// </summary>
    [Serializable]
    [Table(Name = "SDP_SH_PriceBatch")]
    public partial class SDP_SH_PriceBatch_gen
    {
        /// <summary>
        /// 获取或设置ID
        /// </summary>
        [Column(Name = "ID",Length=19),ID,PK]
        public long ID { get; set; }
        /// <summary>
        /// 获取或设置ProductID
        /// </summary>
        [Column(Name = "ProductID",Length=19)]
        public long ProductID { get; set; }
        /// <summary>
        /// 获取或设置PackageID
        /// </summary>
        [Column(Name = "PackageID",Length=19)]
        public long PackageID { get; set; }
        /// <summary>
        /// 获取或设置HotelID
        /// </summary>
        [Column(Name = "HotelID",Length=10)]
        public int HotelID { get; set; }
        /// <summary>
        /// 获取或设置RoomID
        /// </summary>
        [Column(Name = "RoomID",Length=10)]
        public int RoomID { get; set; }
        /// <summary>
        /// 获取或设置RoomPrice
        /// </summary>
        [Column(Name = "RoomPrice",Length=19)]
        public decimal RoomPrice { get; set; }
        /// <summary>
        /// 获取或设置RoomPriceDate
        /// </summary>
        [Column(Name = "RoomPriceDate",Length=23)]
        public DateTime RoomPriceDate { get; set; }
        /// <summary>
        /// 获取或设置TicketID
        /// </summary>
        [Column(Name = "TicketID",Length=10)]
        public int TicketID { get; set; }
        /// <summary>
        /// 获取或设置TicketPrice
        /// </summary>
        [Column(Name = "TicketPrice",Length=19)]
        public decimal TicketPrice { get; set; }
        /// <summary>
        /// 获取或设置TicketPriceDate
        /// </summary>
        [Column(Name = "TicketPriceDate",Length=23)]
        public DateTime TicketPriceDate { get; set; }
        /// <summary>
        /// 获取或设置Version
        /// </summary>
        [Column(Name = "Version",Length=23)]
        public DateTime Version { get; set; }
    }
}