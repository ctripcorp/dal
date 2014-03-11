using System;
using Arch.Data.Orm;

namespace com.ctrip.shard.Entity.DataModel
{
    /// <summary>
    /// django_admin_log
    /// </summary>
    [Serializable]
    [Table(Name = "django_admin_log")]
    public partial class DjangoAdminLogGen
    {
        /// <summary>
        /// 获取或设置id
        /// </summary>
        [Column(Name = "id",Length=10),ID,PK]
        public int Id { get; set; }
        /// <summary>
        /// 获取或设置action_time
        /// </summary>
        [Column(Name = "action_time",Length=19)]
        public DateTime Action_time { get; set; }
        /// <summary>
        /// 获取或设置user_id
        /// </summary>
        [Column(Name = "user_id",Length=10)]
        public int User_id { get; set; }
        /// <summary>
        /// 获取或设置content_type_id
        /// </summary>
        [Column(Name = "content_type_id",Length=10)]
        public int? Content_type_id { get; set; }
        /// <summary>
        /// 获取或设置object_id
        /// </summary>
        [Column(Name = "object_id",Length=2147483647)]
        public string Object_id { get; set; }
        /// <summary>
        /// 获取或设置object_repr
        /// </summary>
        [Column(Name = "object_repr",Length=200)]
        public string Object_repr { get; set; }
        /// <summary>
        /// 获取或设置action_flag
        /// </summary>
        [Column(Name = "action_flag",Length=5)]
        public short Action_flag { get; set; }
        /// <summary>
        /// 获取或设置change_message
        /// </summary>
        [Column(Name = "change_message",Length=2147483647)]
        public string Change_message { get; set; }
    }
}