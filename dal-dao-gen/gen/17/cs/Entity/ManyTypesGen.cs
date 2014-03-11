using System;
using Arch.Data.Orm;

namespace com.ctrip.shard.Entity.DataModel
{
    /// <summary>
    /// ManyTypes
    /// </summary>
    [Serializable]
    [Table(Name = "ManyTypes")]
    public partial class ManyTypesGen
    {
        /// <summary>
        /// 获取或设置Id
        /// </summary>
        [Column(Name = "Id",Length=10),ID,PK]
        public int Id { get; set; }
        /// <summary>
        /// 获取或设置TinyIntCol
        /// </summary>
        [Column(Name = "TinyIntCol",Length=3)]
        public byte? TinyIntCol { get; set; }
        /// <summary>
        /// 获取或设置SmallIntCol
        /// </summary>
        [Column(Name = "SmallIntCol",Length=5)]
        public short? SmallIntCol { get; set; }
        /// <summary>
        /// 获取或设置IntCol
        /// </summary>
        [Column(Name = "IntCol",Length=10)]
        public int? IntCol { get; set; }
        /// <summary>
        /// 获取或设置BigIntCol
        /// </summary>
        [Column(Name = "BigIntCol",Length=19)]
        public long? BigIntCol { get; set; }
        /// <summary>
        /// 获取或设置DecimalCol
        /// </summary>
        [Column(Name = "DecimalCol",Length=10)]
        public decimal? DecimalCol { get; set; }
        /// <summary>
        /// 获取或设置DoubleCol
        /// </summary>
        [Column(Name = "DoubleCol",Length=22)]
        public double? DoubleCol { get; set; }
        /// <summary>
        /// 获取或设置FloatCol
        /// </summary>
        [Column(Name = "FloatCol",Length=12)]
        public float? FloatCol { get; set; }
        /// <summary>
        /// 获取或设置BitCol
        /// </summary>
        [Column(Name = "BitCol",Length=1)]
        public bool? BitCol { get; set; }
        /// <summary>
        /// 获取或设置CharCol
        /// </summary>
        [Column(Name = "CharCol",Length=1)]
        public char? CharCol { get; set; }
        /// <summary>
        /// 获取或设置VarCharCol
        /// </summary>
        [Column(Name = "VarCharCol",Length=45)]
        public string VarCharCol { get; set; }
        /// <summary>
        /// 获取或设置DateCol
        /// </summary>
        [Column(Name = "DateCol",Length=10)]
        public DateTime? DateCol { get; set; }
        /// <summary>
        /// 获取或设置DateTimeCol
        /// </summary>
        [Column(Name = "DateTimeCol",Length=19)]
        public DateTime? DateTimeCol { get; set; }
        /// <summary>
        /// 获取或设置TimeCol
        /// </summary>
        [Column(Name = "TimeCol",Length=8)]
        public TimeSpan? TimeCol { get; set; }
        /// <summary>
        /// 获取或设置TimestampCol
        /// </summary>
        [Column(Name = "TimestampCol",Length=19)]
        public DateTime? TimestampCol { get; set; }
        /// <summary>
        /// 获取或设置YearCol
        /// </summary>
        [Column(Name = "YearCol")]
        public DateTime? YearCol { get; set; }
        /// <summary>
        /// 获取或设置BinaryCol
        /// </summary>
        [Column(Name = "BinaryCol",Length=255)]
        public byte[] BinaryCol { get; set; }
        /// <summary>
        /// 获取或设置BlobCol
        /// </summary>
        [Column(Name = "BlobCol",Length=65535)]
        public byte[] BlobCol { get; set; }
        /// <summary>
        /// 获取或设置LongBlobCol
        /// </summary>
        [Column(Name = "LongBlobCol",Length=2147483647)]
        public byte[] LongBlobCol { get; set; }
        /// <summary>
        /// 获取或设置MediumBlobCol
        /// </summary>
        [Column(Name = "MediumBlobCol",Length=16777215)]
        public byte[] MediumBlobCol { get; set; }
        /// <summary>
        /// 获取或设置TinyBlobCol
        /// </summary>
        [Column(Name = "TinyBlobCol",Length=255)]
        public byte[] TinyBlobCol { get; set; }
        /// <summary>
        /// 获取或设置VarBinaryCol
        /// </summary>
        [Column(Name = "VarBinaryCol",Length=1024)]
        public byte[] VarBinaryCol { get; set; }
        /// <summary>
        /// 获取或设置LongTextCol
        /// </summary>
        [Column(Name = "LongTextCol",Length=2147483647)]
        public string LongTextCol { get; set; }
        /// <summary>
        /// 获取或设置MediumTextCol
        /// </summary>
        [Column(Name = "MediumTextCol",Length=16777215)]
        public string MediumTextCol { get; set; }
        /// <summary>
        /// 获取或设置TextCol
        /// </summary>
        [Column(Name = "TextCol",Length=65535)]
        public string TextCol { get; set; }
        /// <summary>
        /// 获取或设置TinyTextCol
        /// </summary>
        [Column(Name = "TinyTextCol",Length=255)]
        public string TinyTextCol { get; set; }
    }
}