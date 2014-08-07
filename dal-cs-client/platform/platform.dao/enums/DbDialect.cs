using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace platform.dao.enums
{
    /// <summary>
    /// Sql指令类型
    /// </summary>
    public enum DbDialect
    {
        /// <summary>
        /// Sql语句
        /// </summary>
        [Description("Sql server")]
        SqlServer = 0,

        /// <summary>
        /// 存贮过程
        /// </summary>
        [Description("My Sql")]
        MySql = 1
    }
}
