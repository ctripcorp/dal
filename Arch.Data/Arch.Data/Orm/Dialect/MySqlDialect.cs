using Arch.Data.Common.Enums;
using Arch.Data.Orm.sql;
using System;
using System.Collections.Generic;
using System.Text;

namespace Arch.Data.Orm.Dialect
{
    /// <summary>
    /// MySql方言
    /// </summary>
    public class MySqlDialect : DbDialect
    {
        public override Char OpenQuote
        {
            get { return '`'; }
        }

        public override Char CloseQuote
        {
            get { return '`'; }
        }

        public override String IdentitySelectString
        {
            get { return "SELECT LAST_INSERT_ID();\n"; }
        }

        /// <summary>
        /// 拼接操作头部
        /// </summary>
        /// <returns></returns>
        public override String QuoteOpenOpName(String name)
        {
            return name;
        }

        /// <summary>
        /// 加锁类型值
        /// </summary>
        /// <returns></returns>
        public override String WithLock(String parameterName)
        {
            return String.Empty;
        }

        public override String LimitPrefix(Int32 from, Int32 to)
        {
            return String.Empty;
        }

        public override String LimitSuffix(Int32 from, Int32 to)
        {
            if (from > 0 || to > 0)
            {
                StringBuilder sb = new StringBuilder(" LIMIT ");
                sb.Append(from);

                if (to > 0)
                {
                    sb.Append(",")
                      .Append(to);
                }

                return sb.ToString();
            }

            return String.Empty;
        }

        public override String PagingPrefix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending)
        {
            return String.Empty;
        }

        private const String PagingSuffixTemplate = " ORDER BY {0}{1}{2} {3} LIMIT {4},{5}";

        public override String PagingSuffix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending, IList<SqlColumn> sqlColumns)
        {
            StringBuilder sb = new StringBuilder();
            if (pageNumber > 0 && pageSize > 0 && !String.IsNullOrEmpty(orderColumnName))
            {
                sb.AppendFormat(PagingSuffixTemplate,
                    OpenQuote,
                    orderColumnName,
                    CloseQuote,
                    isAscending ? String.Empty : OrderDirection.DESC.ToString(),
                    (pageNumber - 1) * pageSize,
                    pageSize);
            }
            return sb.ToString();
        }

    }
}
