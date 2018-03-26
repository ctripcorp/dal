using Arch.Data.DbEngine;
using System;
using System.Collections;
using System.Data;
using System.Linq;

namespace Arch.Data.Orm.sql
{
    class SqlConstraint : IConstraint
    {
        protected String Symbol;
        private String column;
        private String op;
        private Object val;
        private Object val2;
        private SqlQuery query;
        protected Int32 index;
        protected Int32 index2;
        private const String Between = "BETWEEN";

        public SqlConstraint(String column, String op, Object val)
        {
            this.column = column.ToLower();
            this.op = op;
            this.val = val;
        }

        public SqlConstraint(String column, String op, Object val, Object val2)
        {
            this.column = column.ToLower();
            this.op = op;
            this.val = val;
            this.val2 = val2;
        }

        public SqlConstraint(IQuery query)
        {
            this.query = (SqlQuery)query;
        }

        protected SqlConstraint() { }

        public String Column
        {
            get { return column; }
        }

        public String Operator
        {
            get { return op; }
        }

        public Object Value
        {
            get { return val; }
            set { val = value; }
        }

        public Object Value2
        {
            get { return val2; }
            set { val2 = value; }
        }

        public Boolean HasQuery
        {
            get { return query != null; }
        }

        public IQuery Query
        {
            get { return query; }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="table"></param>
        /// <param name="openQuote"></param>
        /// <param name="closeQuote"></param>
        /// <param name="offset"></param>
        /// <returns></returns>
        public virtual String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            if (!String.IsNullOrEmpty(Symbol)) return String.Format("{0}", Symbol);

            if (HasQuery)
            {
                String sql = query.GetQuerySql(table, openQuote, closeQuote, ref offset);
                return String.Format("({0})", sql);
            }
            else
            {
                index = offset;
                offset += 1;
                String name = table.Translate(column);
                if (String.IsNullOrEmpty(name)) throw new DalException(String.Format("Table {0} does not contain attribute {1}.", table.Name, column));

                String result;
                if (op.ToUpper().IndexOf(Between, StringComparison.Ordinal) > -1)
                {
                    index2 = index + 1;
                    result = String.Format("{0}{1}{2} {3} @{4} AND @{5}", openQuote, name, closeQuote, op, index, index2);
                    offset++;
                }
                else
                {
                    result = String.Format(openQuote + "{0}" + closeQuote + "{1}@{2}", name, op, index);
                }

                return result;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="table"></param>
        /// <param name="parameters"></param>
        public virtual void SetParameters(SqlTable table, StatementParameterCollection parameters)
        {
            if (HasQuery)
            {
                query.SetParameters(table, parameters);
            }
            else
            {
                IColumn sqlColumn = table.ColumnList.FirstOrDefault(item => item.Name.Equals(column, StringComparison.OrdinalIgnoreCase));
                DbType? dataType = sqlColumn != null ? sqlColumn.ColumnType : null;

                StatementParameter parameter1 = new StatementParameter { Name = index.ToString() };
                SqlUtils.PrepareParameter(parameter1, val, dataType);
                parameters.Add(parameter1);

                if (op.ToUpper().IndexOf(Between, StringComparison.Ordinal) > -1)
                {
                    StatementParameter parameter2 = new StatementParameter { Name = index2.ToString() };
                    SqlUtils.PrepareParameter(parameter2, val2, dataType);
                    parameters.Add(parameter2);
                }
            }
        }

        public virtual void SetInParameters(SqlTable table, StatementParameterCollection parameters)
        {
            if (HasQuery)
            {
                query.SetParameters(table, parameters);
            }
            else
            {
                var values = (IList)Value;
                if (values != null && values.Count > 0)
                {
                    IColumn sqlColumn = table.ColumnList.FirstOrDefault(item => item.Name.Equals(column, StringComparison.OrdinalIgnoreCase));
                    DbType? dataType = sqlColumn != null ? sqlColumn.ColumnType : null;
                    for (Int32 i = 0; i < values.Count; i++)
                    {
                        StatementParameter parameter = new StatementParameter { Name = index.ToString() };
                        SqlUtils.PrepareParameter(parameter, values[i], dataType);
                        parameters.Add(parameter);
                        index++;
                    }
                }
            }
        }

        public virtual Boolean IsOperator()
        {
            return false;
        }

        public virtual Boolean IsBracket()
        {
            return false;
        }

        public virtual Boolean IsClause()
        {
            return true;
        }

        public virtual Boolean IsNull()
        {
            return false;
        }

    }
}
