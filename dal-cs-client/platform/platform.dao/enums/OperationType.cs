using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace platform.dao.enums
{
    /// <summary>
    /// 读写分离时用来区分操作类型
    /// </summary>
    public enum OperationType
    {
        /// <summary>
        /// 未指定，默认为主数据库，即可读可写
        /// </summary>
        [Description("未指定")]
        Default = 0,

        /// <summary>
        /// 读操作
        /// </summary>
        [Description("读操作")]
        Read = 1,

        /// <summary>
        /// 写操作
        /// </summary>
        [Description("写操作")]
        Write = 2
    }
}
