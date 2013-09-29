using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.orm.attribute;
using System.Reflection;
using System.Data;
using platform.dao.log;
using platform.dao.orm;
using platform.dao.utils;

namespace platform.dao.client
{
    /// <summary>
    /// 
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public abstract class AbstractDAO<T> : IDAO<T>
    {
        private static ILoggerAdapter logger = LogFactory.GetLogger(typeof(AbstractDAO<T>).Name);

        public abstract IDataReader FetchBySql(string sql);

        /// <summary>
        /// 根据自增主键，获取对应的实体对象
        /// </summary>
        /// <param name="iD">自增主键</param>
        /// <returns>实体对象</returns>
        public virtual T FindByPk(int iD)
        {
            Type type = typeof(T);

            PropertyInfo[] fields = type.GetProperties(
                BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);

            Dictionary<string, PropertyInfo> columnFieldsMap = new Dictionary<string, PropertyInfo>();

            string pk = "";
            IList<SqlColumn> columns = new List<SqlColumn>();

            foreach (PropertyInfo field in fields)
            {
                ColumnAttribute colAttr = (ColumnAttribute)
                    field.GetCustomAttributes(typeof(ColumnAttribute), false)[0];

                if (field.IsDefined(typeof(PrimaryKeyAttribute), false))
                    pk = colAttr.Name;

                columnFieldsMap.Add(colAttr.Name, field);

                SqlColumn column = new SqlColumn() { Name=colAttr.Name, Alias = colAttr.Alias};
                columns.Add(column);
            }

            //Get table name
            TableAttribute tableAttr = (TableAttribute) type.GetCustomAttributes(typeof(TableAttribute), false)[0];

            SqlTable table = new SqlTable() { Schema = tableAttr.Schema, Name = tableAttr.Name, Columns = columns};

            StringBuilder sql = new StringBuilder(table.GetSelectAllSql());

            sql.Append(" WHERE ");

            sql.Append(pk);

            sql.Append(" = ");

            sql.Append(iD);

            T obj = default(T);

            logger.Warn(sql.ToString());

            using (IDataReader reader = this.FetchBySql(sql.ToString()))
            {
                if (reader.Read())
                {
                    obj = Activator.CreateInstance<T>();
                    foreach (var key in columnFieldsMap.Keys)
                    {
                        PropertyInfo p = columnFieldsMap[key];
                        object convertedValue = reader[key];
                        //Type underlyingType = Nullable.GetUnderlyingType(p.PropertyType);

                        //if (underlyingType != null)
                        //{
                        //    convertedValue = TypeConverter.ConvertToUnderlyingType(underlyingType, convertedValue);
                        //}
                        p.SetValue(obj, convertedValue, null);
                    }
                }
            }

            return obj;
        }

        public virtual int DeleteByPk(int iD)
        {
            throw new NotImplementedException();
        }

        public virtual int Delete(T entity)
        {
            throw new NotImplementedException();
        }

        public virtual int Insert(T entity)
        {
            throw new NotImplementedException();
        }

        public virtual int BatchInsert(IList<T> entities)
        {
            throw new NotImplementedException();
        }

        public virtual int Update(T entity)
        {
            throw new NotImplementedException();
        }

        public virtual IList<T> GetAll()
        {
            throw new NotImplementedException();
        }

        public virtual int DeleteAll()
        {
            throw new NotImplementedException();
        }
    }
}
