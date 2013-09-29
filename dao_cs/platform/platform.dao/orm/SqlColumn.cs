using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.orm
{
    public class SqlColumn
    {

        /// <summary>
        /// 字段名
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// 字段别名
        /// </summary>
        public string Alias { get; set; }

    }
}
