using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.orm.attribute;
using System.Reflection;
using System.Data;
using platform.dao.log;

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

        public virtual T FindByPk(int iD)
        {
            Type type = typeof(T);

            StringBuilder sql = new StringBuilder("SELECT ");

            PropertyInfo[] fields = type.GetProperties(
                BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);

            Dictionary<string, PropertyInfo> columnFieldsMap = new Dictionary<string, PropertyInfo>();

            string pk = "";

            foreach (PropertyInfo field in fields)
            {
                ColumnAttribute colAttr = (ColumnAttribute)
                    field.GetCustomAttributes(typeof(ColumnAttribute), false)[0];

                if (field.IsDefined(typeof(PrimaryKeyAttribute), false))
                    pk = colAttr.Name;

                columnFieldsMap.Add(colAttr.Name, field);

                sql.Append(colAttr.Name);
                sql.Append(",");
            }

            if (fields.Length > 0)
                sql.Remove(sql.Length - 1, 1);

            sql.Append(" FROM ");

            //Get table name
            TableAttribute tableAttr = (TableAttribute) type.GetCustomAttributes(typeof(TableAttribute), false)[0];
            
            if (!string.IsNullOrEmpty(tableAttr.Schema))
            {
                sql.Append(tableAttr.Schema);
            }
            else
            {
                sql.Append("dbo");
            }
            sql.Append(".");

            sql.Append(tableAttr.Name);

            sql.Append(" WHERE ");

            sql.Append(pk);

            sql.Append(" = ");

            sql.Append(iD);

            T obj = Activator.CreateInstance<T>();

            logger.Warn(sql.ToString());

            using (IDataReader reader = this.FetchBySql(sql.ToString()))
            {
                if (reader.Read())
                {
                    foreach (var key in columnFieldsMap.Keys)
                    {
                        PropertyInfo p = columnFieldsMap[key];
                        object convertedValue = reader[key];
                        Type underlyingType = Nullable.GetUnderlyingType(p.PropertyType);

                        if (underlyingType != null)
                        {
                            try
                            {
                                convertedValue = System.Convert.ChangeType(convertedValue,
                                    underlyingType);
                            }
                            catch (Exception ex)
                            {
                                // the input string could not be converted to the target type - abort
                                logger.Error(ex.StackTrace);
                                if (underlyingType == typeof(DateTime))
                                {
                                    ulong milliseconds = (ulong)convertedValue;
                                    DateTime dtDateTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
                                    convertedValue = dtDateTime.AddMilliseconds(milliseconds);
                                }
                            }
                        }
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
