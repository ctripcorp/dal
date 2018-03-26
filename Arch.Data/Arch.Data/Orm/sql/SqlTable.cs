using Arch.Data.DbEngine;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace Arch.Data.Orm.sql
{
    public class SqlTable : ITable
    {
        #region Fields

        protected Type type;
        protected String name;
        protected String schema;
        protected List<SqlColumn> columns;
        protected SqlColumn identity;
        protected SqlColumn pkColumn;
        protected List<SqlColumn> pkColumns;
        protected List<SqlTrigger> triggers;
        protected Boolean isSPResult;
        protected String shardColumn;

        #endregion

        public SqlTable(Type type, String name, String schema)
        {
            this.type = type;
            this.name = name;
            this.schema = schema;
            columns = new List<SqlColumn>();
            pkColumns = new List<SqlColumn>();
            triggers = new List<SqlTrigger>();
            isSPResult = String.IsNullOrEmpty(name);
        }

        public Type Class
        {
            get { return type; }
        }

        public String Name
        {
            get { return name; }
        }

        public String Schema
        {
            get { return schema; }
        }

        public SqlColumn PkColumn
        {
            get { return pkColumn; }
        }

        public IColumn[] Columns
        {
            get { return columns.ToArray(); }
        }

        public IList<SqlColumn> PkColumns
        {
            get { return pkColumns; }
        }

        public List<SqlColumn> ColumnList
        {
            get { return columns; }
        }

        public IColumn Identity
        {
            get { return identity; }
        }

        public String ShardColumn
        {
            get { return shardColumn; }
            set { shardColumn = value; }
        }

        public virtual void Add(SqlColumn column)
        {
            if (column.IsID)
            {
                if (identity != null)
                    throw new DalException(String.Format("Can't add identity column {0} to table {1}, it already has an identity column {2}.", column.Name, Name, identity.Name));

                identity = column;
            }

            columns.Add(column);

            if (column.IsPK) pkColumns.Add(column);
            column.Ordinal = columns.Count;
        }

        public virtual void AddTrigger(SqlTrigger trigger)
        {
            triggers.Add(trigger);
        }

        public SqlColumn ByName(String columnName)
        {
            if (String.IsNullOrEmpty(columnName)) return null;
            String target = columnName.ToLower();
            return columns.FirstOrDefault(column => column.Name.Equals(target));
        }

        public SqlColumn ByAlias(String alias)
        {
            if (String.IsNullOrEmpty(alias)) return null;
            String target = alias.ToLower();
            return columns.FirstOrDefault(column => column.Alias.Equals(target));
        }

        public String Translate(String alias)
        {
            var column = ByAlias(alias);
            return column == null ? null : column.Name;
        }

        public static void AddSqlToExtendParams(Statement statement, IDictionary extendParams)
        {
            if (extendParams == null) return;
            var types = extendParams.GetType().GetGenericArguments();

            //仅当extendParams是 Dictionary<string, object> 或者 Dictionary<string, string>时，
            //才将SQL填回
            if (types.Length != 2 || types[0] != typeof(String) || (types[1] != typeof(String) && types[1] != typeof(Object))) return;
            extendParams[Common.constant.DALExtStatementConstant.SQL] = statement.StatementText;
        }

    }
}
