using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Text;
using platform.dao.exception;
using platform.dao.log;
using platform.dao.orm;
using platform.dao.param;

namespace platform.dao.client
{
    /// <summary>
    /// 
    /// </summary>
    /// <typeparam name="T"></typeparam>    
    public abstract class AbstractDAO : IDAO
    {
        private static ILoggerAdapter logger = LogFactory.GetLogger(typeof(AbstractDAO).Name);

        //public static IClient client;

        //protected void Init(bool useDas = true, string DbName = null, string credential = null,
        //    string provider = null, string connectString = null)
        static AbstractDAO()
        {
            //Reload(useDas, DbName, credential, provider, connectString);
            bool initSuccess = false;
            try
            {
                string dbName = ConfigurationManager.AppSettings["DbName"];
                string cred = ConfigurationManager.AppSettings["Credential"];

                ClientPool.GetInstance().CreateDasClient(dbName, cred);
                ClientPool.GetInstance().DefaultName = dbName;
                initSuccess = true;
            }
            catch
            {
            }
            try
            {
                string providername = ConfigurationManager.ConnectionStrings["platform"].ProviderName;
                string connString = ConfigurationManager.ConnectionStrings["platform"].ConnectionString;

                ClientPool.GetInstance().CreateDbClient("platform", providername, connString);
                ClientPool.GetInstance().DefaultName = "platform";
                initSuccess = true;
            }
            catch
            {
            }
            if(!initSuccess)
                throw new DAOConfigException(
                    "Please ensure appSettings and connectionStrings of name platform exists!");
        }

        ///// <summary>
        ///// 重新加载配置
        ///// </summary>
        //public static void Reload(bool useDas=true, string DbName=null, string credential=null,
        //    string provider=null,string connectString=null)
        //{
        //    try
        //    {
        //        if (useDas)
        //        {
        //            ClientPool.GetInstance().CreateDasClient(
        //                string.IsNullOrEmpty(DbName) ? ConfigurationManager.AppSettings["DbName"] : DbName,
        //                string.IsNullOrEmpty(credential) ? ConfigurationManager.AppSettings["Credential"] : credential);
        //        }
        //        else
        //        {
        //            ClientPool.GetInstance().CreateDbClient("platform",
        //                string.IsNullOrEmpty(provider) ? ConfigurationManager.ConnectionStrings["platform"].ProviderName : provider,
        //                string.IsNullOrEmpty(connectString) ? ConfigurationManager.ConnectionStrings["platform"].ConnectionString : connectString
        //                );
        //        }
        //    }
        //    catch (ArgumentNullException ex)
        //    {
        //        throw new DAOConfigException(
        //            "Please ensure appSettings and connectionStrings of name platform exists!");
        //    }
        //}

        /// <summary>
        /// 根据自增主键，获取对应的实体对象
        /// </summary>
        /// <param name="iD">自增主键</param>
        /// <returns>实体对象</returns>
        public virtual T FetchByPk<T>(int iD)
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            StringBuilder sql = new StringBuilder(table.GetSelectAllSql());

            foreach (SqlColumn col in table.Columns)
            {
                if (col.IsPrimaryKey)
                {
                    sql.Append(" WHERE ")
                        .Append(col.Name)
                        .Append(" = ")
                        .Append(iD);
                    break;
                }
            }

            T obj = default(T);

            logger.Warn(sql.ToString());

            using (IDataReader reader = ClientPool.GetInstance().GetCurrentClient().Fetch(sql.ToString()))
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
                    sql.Append(" WHERE ")
                         .Append(col.Name)
                         .Append(" = ")
                         .Append(iD);
                    break;
                }
            }

            logger.Warn(sql.ToString());

            return ClientPool.GetInstance().GetCurrentClient().Execute(sql.ToString());
        }

        /// <summary>
        /// 插入一条数据
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="entity"></param>
        /// <returns></returns>
        public virtual int Insert<T>(T entity)
        {
            SqlTable table = SqlTable.CreateInstance(entity.GetType());

            //StringBuilder sql = new StringBuilder(table.GetInsertSql());

            IList<IParameter> parameters = new List<IParameter>();
            foreach (SqlColumn col in table.Columns)
            {
                parameters.Add(ParameterFactory.CreateValue(string.Format("@{0}", col.Name),
                    col.GetValue(entity), index: col.Index));
            }

            return ClientPool.GetInstance().GetCurrentClient().Execute(table.GetInsertSql(), parameters.ToArray());
            ;
        }

        /// <summary>
        /// 批量插入多条数据
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="entities"></param>
        /// <returns></returns>
        public virtual int BatchInsert<T>(IList<T> entities)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// 根据主键更新一条数据
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="entity"></param>
        /// <returns></returns>
        public virtual int Update<T>(T entity)
        {
            SqlTable table = SqlTable.CreateInstance(entity.GetType());

            StringBuilder sql = new StringBuilder(table.GetUpdateSql());

            IList<IParameter> parameters = new List<IParameter>();
            foreach (SqlColumn col in table.Columns)
            {
                if (col.IsPrimaryKey)
                {
                    sql.Append(" WHERE ")
                         .Append(col.Name)
                         .Append(" = ")
                         .Append(col.GetValue(entity));
                }
                parameters.Add(ParameterFactory.CreateValue(string.Format("@{0}", col.Name),
                   col.GetValue(entity), index: col.Index));
            }

            logger.Warn(sql.ToString());

            return ClientPool.GetInstance().GetCurrentClient().Execute(sql.ToString());
        }

        /// <summary>
        /// 获取一个表的所有记录
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <returns></returns>
        public virtual IList<T> FetchAll<T>()
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            StringBuilder sql = new StringBuilder(table.GetSelectAllSql());

            IList<T> results = new List<T>();

            logger.Warn(sql.ToString());

            using (IDataReader reader = ClientPool.GetInstance().GetCurrentClient().Fetch(sql.ToString()))
            {
                while (reader.Read())
                {
                    T obj = default(T);
                    obj = Activator.CreateInstance<T>();
                    foreach (var col in table.Columns)
                    {
                        object convertedValue = reader[col.Name];
                        col.SetValue(obj, convertedValue);
                    }
                    results.Add(obj);
                }
            }

            return results;
        }

        /// <summary>
        /// 删除一个表的所有记录
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <returns></returns>
        public virtual int DeleteAll<T>()
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            logger.Warn(table.GetDeleteSql());

            return ClientPool.GetInstance().GetCurrentClient().Execute(table.GetDeleteSql());
        }

        public IDataReader Fetch(string sql, params IParameter[] parameters)
        {
            return ClientPool.GetInstance().GetCurrentClient().Fetch(sql, parameters);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <returns></returns>
        public IList<T> Fetch<T>(string sql, params IParameter[] parameters)
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            IList<T> results = new List<T>();

            using (IDataReader dr = ClientPool.GetInstance().GetCurrentClient().Fetch(sql, parameters))
            {
                while (dr.Read())
                {
                    T obj = Activator.CreateInstance<T>();
                    foreach (var col in table.Columns)
                    {
                        object convertedValue = dr[col.Name];
                        col.SetValue(obj, convertedValue);
                    }
                    results.Add(obj);
                }
            }

            return results;
        }

        public int Execute(string sql, params IParameter[] parameters)
        {
            return ClientPool.GetInstance().GetCurrentClient().Execute(sql, parameters);
        }

        public IDataReader FetchBySp(string sp, params IParameter[] parameters)
        {
            return ClientPool.GetInstance().GetCurrentClient().FetchBySp(sp, parameters);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <returns></returns>
        public IList<T> FetchBySp<T>(string sp, params IParameter[] parameters)
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            IList<T> results = new List<T>();

            using (IDataReader dr = ClientPool.GetInstance().GetCurrentClient().FetchBySp(sp, parameters))
            {
                while (dr.Read())
                {
                    T obj = Activator.CreateInstance<T>();
                    foreach (var col in table.Columns)
                    {
                        object convertedValue = dr[col.Name];
                        col.SetValue(obj, convertedValue);
                    }
                    results.Add(obj);
                }
            }

            return results;
        }

        public int ExecuteSp(string sp, params IParameter[] parameters)
        {
            return ClientPool.GetInstance().GetCurrentClient().ExecuteSp(sp, parameters);
        }
    }
}
