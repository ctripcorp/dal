using System;

namespace Arch.Data.Orm.sql
{
    class NullConstraint : SqlConstraint
    {
        public NullConstraint(String column, Boolean isNull) : base(column, isNull ? "is" : "is not", null) { }

        public override Boolean IsClause()
        {
            return true;
        }

        public override String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            var name = table.Translate(Column);
            if (String.IsNullOrEmpty(name)) throw new DalException(String.Format("Table {0} doesn't contain column {1}.", table.Name, Column));

            index = offset;
            offset++;
            return String.Format(openQuote + "{0}" + closeQuote + " {1} null", name, Operator);
        }

    }
}
