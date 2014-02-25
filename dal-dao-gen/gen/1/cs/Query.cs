using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Dao
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [Table(Name = "")]
    public partial class Query
    {
        /// <summary>
        /// 获取或设置id
        /// </summary>
        [Column(Name = "id",Length=11)]
        public uint Id { get; set; }
        /// <summary>
        /// 获取或设置driver
        /// </summary>
        [Column(Name = "driver",Length=255)]
        public string Driver { get; set; }
        /// <summary>
        /// 获取或设置server
        /// </summary>
        [Column(Name = "server",Length=255)]
        public string Server { get; set; }
        /// <summary>
        /// 获取或设置port
        /// </summary>
        [Column(Name = "port",Length=11)]
        public uint Port { get; set; }
        /// <summary>
        /// 获取或设置domain
        /// </summary>
        [Column(Name = "domain",Length=45)]
        public string Domain { get; set; }
        /// <summary>
        /// 获取或设置user
        /// </summary>
        [Column(Name = "user",Length=255)]
        public string User { get; set; }
        /// <summary>
        /// 获取或设置password
        /// </summary>
        [Column(Name = "password",Length=255)]
        public string Password { get; set; }
        /// <summary>
        /// 获取或设置db_type
        /// </summary>
        [Column(Name = "db_type",Length=45)]
        public string Db_type { get; set; }
    }
}