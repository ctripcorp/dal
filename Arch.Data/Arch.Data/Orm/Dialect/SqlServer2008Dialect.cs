using Arch.Data.Common.Enums;
using Arch.Data.Orm.sql;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Arch.Data.Orm.Dialect
{
    /// <summary>
    /// SqlServer2008方言
    /// </summary>
    public class SqlServer2008Dialect : DbDialect
    {
        public override String IdentitySelectString
        {
            get { return "SELECT SCOPE_IDENTITY();\n"; }
        }

        /// <summary>
        /// 拼接操作头部
        /// </summary>
        /// <returns></returns>
        public override String QuoteOpenOpName(String name)
        {
            return String.Concat("SET NOCOUNT OFF;", name);
        }

        /// <summary>
        /// 拼接操作尾部
        /// </summary>
        /// <returns></returns>
        public override String QuoteCloseOpName()
        {
            return "SET NOCOUNT ON;";
        }

        /// <summary>
        /// 加锁类型值
        /// </summary>
        /// <param name="parameterName"></param>
        /// <returns></returns>
        public override String WithLock(String parameterName)
        {
            if (parameterName == null) return " WITH (NOLOCK)";
            if (parameterName.Length == 0) return String.Empty;

            return String.Concat(" WITH (", parameterName, ")");
        }

        public override String LimitPrefix(Int32 from, Int32 to)
        {
            if (from > 0) return String.Format(" TOP({0}) ", from);
            return String.Empty;
        }

        public override String LimitSuffix(Int32 from, Int32 to)
        {
            return String.Empty;
        }

        private const String PagingPrefixTemplate = "WITH CTE AS (SELECT ROW_NUMBER() OVER(ORDER BY {0} {1}) AS ROWNUM, ";

        public override String PagingPrefix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending)
        {
            StringBuilder sb = new StringBuilder();
            if (pageNumber > 0 && pageSize > 0 && !String.IsNullOrEmpty(orderColumnName))
            {
                sb.AppendFormat(PagingPrefixTemplate,
                    orderColumnName,
                    isAscending ? String.Empty : OrderDirection.DESC.ToString());
            }
            return sb.ToString();
        }

        private const String PagingSuffixTemplate = ")SELECT {0} FROM CTE WHERE ROWNUM BETWEEN {1} AND {2} ";

        public override String PagingSuffix(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending, IList<SqlColumn> sqlColumns)
        {
            StringBuilder sb = new StringBuilder();
            if (pageNumber > 0 && pageSize > 0 && sqlColumns != null && sqlColumns.Count > 0)
            {
                sb.AppendFormat(PagingSuffixTemplate,
                    String.Join(",", sqlColumns.Select(p => String.Format("{0}{1}{2}", OpenQuote, p.Name, CloseQuote))),
                    (pageNumber - 1) * pageSize + 1,
                    pageSize * pageNumber);
            }
            return sb.ToString();
        }

    }
}
