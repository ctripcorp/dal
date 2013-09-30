using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.exception;

namespace platform.dao.orm
{
    public class SqlTable
    {
        /// <summary>
        /// 数据库表的所有字段
        /// </summary>
        public IList<SqlColumn> Columns { get; set; }

        /// <summary>
        /// 空则默认
        /// </summary>
        public string Schema { get; set; }

        /// <summary>
        /// 表名
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// Select * From Tablename
        /// </summary>
        /// <returns></returns>
        public string GetSelectAllSql()
        {
            StringBuilder sb = new StringBuilder("SELECT ");

            if (Columns.Count < 1)
            {
                throw new DAOException(string.Format(
                        "The columns of table {0} does not exists!", Name));
            }

            foreach (SqlColumn col in Columns)
            {
                sb.Append(col.Name);
                sb.Append(",");
            }

            sb.Remove(sb.Length - 1, 1);
            sb.Append(" FROM ");

            if (!string.IsNullOrEmpty(Schema))
            {
                sb.Append(Schema);
                sb.Append(".");
            }

            sb.Append(Name);

            return sb.ToString();
        }

        public static SqlTable CreateInstance(Type type)
        {
        }

    }
}
