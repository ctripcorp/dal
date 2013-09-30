using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.exception;
using System.Reflection;
using platform.dao.orm.attribute;

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

        /// <summary>
        /// 获取删除语句
        /// </summary>
        /// <returns></returns>
        public string GetDeleteSql()
        {
            StringBuilder sb = new StringBuilder("DELETE FROM ");

            if (!string.IsNullOrEmpty(Schema))
            {
                sb.Append(Schema);
                sb.Append(".");
            }

            sb.Append(Name);

            return sb.ToString();
        }

        /// <summary>
        /// 从类型创建SqlTable
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static SqlTable CreateInstance(Type type)
        {
            PropertyInfo[] fields = type.GetProperties(
               BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);

            IList<SqlColumn> columns = new List<SqlColumn>();

            foreach (PropertyInfo field in fields)
            {
                ColumnAttribute colAttr = (ColumnAttribute)
                    field.GetCustomAttributes(typeof(ColumnAttribute), false)[0];

                SqlColumn column = new SqlColumn() { Name = colAttr.Name, Alias = colAttr.Alias, PropertyInfo=field};

                if (field.IsDefined(typeof(PrimaryKeyAttribute), false))
                    column.IsPrimaryKey = true;

                columns.Add(column);
            }

            //Get table name
            TableAttribute tableAttr = (TableAttribute)type.GetCustomAttributes(typeof(TableAttribute), false)[0];

            SqlTable table = new SqlTable() { Schema = tableAttr.Schema, Name = tableAttr.Name, Columns = columns };

            return table;
        }

    }
}
