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
    public abstract class AbstractDAO : IDAO
    {
        private static ILoggerAdapter logger = LogFactory.GetLogger(typeof(AbstractDAO).Name);

        public abstract IDataReader FetchBySql(string sql);

        public abstract int ExecuteSql(string sql);

        /// <summary>
        /// 根据自增主键，获取对应的实体对象
        /// </summary>
        /// <param name="iD">自增主键</param>
        /// <returns>实体对象</returns>
        public virtual T FindByPk<T>(int iD)
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            StringBuilder sql = new StringBuilder(table.GetSelectAllSql());

            foreach(SqlColumn col in table.Columns)
            {
                if (col.IsPrimaryKey)
                {
                    sql.Append(" WHERE ");
                    sql.Append(col.Name);
                    sql.Append(" = ");
                    sql.Append(iD);
                    break;
                }
            }

            T obj = default(T);

            logger.Warn(sql.ToString());

            using (IDataReader reader = this.FetchBySql(sql.ToString()))
            {
                if (reader.Read())
                {
                    obj = Activator.CreateInstance<T>();
                    foreach (var col in table.Columns)
                    {
                        object convertedValue = reader[col.Name];
                        col.SetValue(obj, convertedValue);
                    }
                }
            }

            return obj;
        }

        /// <summary>
        /// 根据自增主键，删除数据
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="iD"></param>
        /// <returns></returns>
        public virtual int DeleteByPk<T>(int iD)
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            StringBuilder sql = new StringBuilder(table.GetDeleteSql());

            foreach (SqlColumn col in table.Columns)
            {
                if (col.IsPrimaryKey)
                {
                    sql.Append(" WHERE ");
                    sql.Append(col.Name);
                    sql.Append(" = ");
                    sql.Append(iD);
                    break;
                }
            }

            logger.Warn(sql.ToString());

            return this.ExecuteSql(sql.ToString());
        }

        public virtual int Delete<T>(T entity)
        {
            throw new NotImplementedException();
        }

        public virtual int Insert<T>(T entity)
        {
            throw new NotImplementedException();
        }

        public virtual int BatchInsert<T>(IList<T> entities)
        {
            throw new NotImplementedException();
        }

        public virtual int Update<T>(T entity)
        {
            throw new NotImplementedException();
        }

        public virtual IList<T> GetAll<T>()
        {
            throw new NotImplementedException();
        }

        public virtual int DeleteAll<T>()
        {
            throw new NotImplementedException();
        }
    }
}
