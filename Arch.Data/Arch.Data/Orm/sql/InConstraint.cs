using System;
using System.Collections;
using System.Text;

namespace Arch.Data.Orm.sql
{
    class InConstraint : SqlConstraint
    {
        public InConstraint(String column, String op, IEnumerable values) : base(column, op, values) { }

        public override Boolean IsClause()
        {
            return true;
        }

        /// <summary>
        /// todo:optimize
        /// </summary>
        /// <param name="table"></param>
        /// <param name="openQuote"></param>
        /// <param name="closeQuote"></param>
        /// <param name="offset"></param>
        /// <returns></returns>
        public override String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            String name = table.Translate(Column);
            if (String.IsNullOrEmpty(name))
                throw new DalException(String.Format("Column {0} not found in table {1}", Column, table.Name));

            index = offset;
            IList values = (IList)Value;
            StringBuilder buf = new StringBuilder();
            buf.Append(openQuote).Append(name).Append(closeQuote).Append(" ").Append(Operator).Append(" (");
            if (values.Count > 0)
            {
                offset = offset + values.Count;
                for (Int32 i = index; i < offset; i++)
                {
                    if (i > index) buf.Append(",");
                    buf.Append(String.Format("@{0}", i));
                }
            }
            else
            {
                buf.Append("null");
            }
            buf.Append(")");
            return buf.ToString();
        }

    }
}
