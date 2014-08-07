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
    public enum StatementType
    {
        /// <summary>
        /// Sql语句
        /// </summary>
        [Description("Sql语句")]
        Sql = 0,

        /// <summary>
        /// 存贮过程
        /// </summary>
        [Description("存贮过程")]
        StoredProcedure = 1
    }
}
