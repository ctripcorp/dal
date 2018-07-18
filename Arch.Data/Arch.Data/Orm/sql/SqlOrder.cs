using System;
using System.Text;

namespace Arch.Data.Orm.sql
{
    class SqlOrder
    {
        public SqlOrder(String column, Boolean asc)
        {
            Column = column.ToLower();
            Ascending = asc;
        }

        public String Column { get; private set; }

        public Boolean Ascending { get; private set; }

        public String GetSql(SqlTable table, Char openQuote, Char closeQuote)
        {
            String name = table.Translate(Column);
            if (String.IsNullOrEmpty(name))
                throw new DalException(String.Format("Table {0} does not contain attribute {1}.", table.Name, Column));

            String dir = Ascending ? "asc" : "desc";
            StringBuilder buf = new StringBuilder();
            buf.Append(openQuote).Append(name).Append(closeQuote);
            return string.Format("{0} {1}", buf, dir);
        }
    }
}
