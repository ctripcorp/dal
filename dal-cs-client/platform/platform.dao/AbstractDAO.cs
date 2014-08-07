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
using platform.dao.client;
using platform.dao.utils;

namespace platform.dao
{
    /// <summary>
    /// 
    /// </summary>
    /// <typeparam name="T"></typeparam>    
    public abstract class AbstractDAO : IDAO
    {
        private static ILogAdapter logger = LogFactory.GetLogger(typeof(AbstractDAO).Name);

        private static Dictionary<string, IClient> clients = new Dictionary<string,IClient>();

        private static object lock_obj = new object();

        protected string PhysicDbName = string.Empty;

        protected int ServicePort = 9000;

        protected string CredentialID = "30303";

        private IClient GetClient(string name)
        {
            lock (lock_obj)
            {
                if (clients.ContainsKey(name))
                {
                    return clients[name];
                }
                return null;
            }
        }

        public void Init()
        {
            if (string.IsNullOrEmpty(PhysicDbName))
            {
                string dbName = ConfigurationManager.AppSettings["DirectDbName"];
                string providername = ConfigurationManager.ConnectionStrings[dbName].ProviderName;
                string connString = ConfigurationManager.ConnectionStrings[dbName].ConnectionString;

                lock (lock_obj)
                {
                    //clients.Add(string.Empty, new DbClient(providername, connString));
                    clients[string.Empty] = new DbClient(providername, connString);
                }
            }
            else
            {
                DasClient client = new DasClient() { 
                    PhysicDbName = PhysicDbName,
                    CredentialID = CredentialID,
                    ServicePort = ServicePort
                };

                client.Init();
              
                lock (lock_obj)
                {
                   //clients.Add(PhysicDbName, client);
                    clients[PhysicDbName] = client;
                }
            }
        }

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

            using (IDataReader reader = GetClient(PhysicDbName).Fetch(sql.ToString(), null))
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

            return GetClient(PhysicDbName).Execute(sql.ToString(), null);
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
                if (!col.IsPrimaryKey)
                {
                    parameters.Add(new ConcreteParameter()
                    {
                        DbType = TypeConverter.ResolveType(col.PropertyInfo.PropertyType),
                        Name = string.Format("@{0}", col.Name),
                        Value = col.GetValue(entity),
                        Index = col.Index,
                        Direction = ParameterDirection.Input
                    });
                }
            }

            return GetClient(PhysicDbName).Execute(table.GetInsertSql(), parameters.ToArray());
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
                parameters.Add(new ConcreteParameter()
                {
                    DbType = TypeConverter.ResolveType(col.PropertyInfo.PropertyType),
                    Name = string.Format("@{0}", col.Name),
                    Value = col.GetValue(entity),
                    Index = col.Index
                });
            }

            logger.Warn(sql.ToString());

            return GetClient(PhysicDbName).Execute(sql.ToString(), null);
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

            using (IDataReader reader = GetClient(PhysicDbName).Fetch(sql.ToString(), null))
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

            return GetClient(PhysicDbName).Execute(table.GetDeleteSql(), null);
        }

        public IDataReader Fetch(string sql, IParameter[] parameters, bool masterOnly = true)
        {
            return GetClient(PhysicDbName).Fetch(sql, parameters, masterOnly);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <returns></returns>
        public IList<T> Fetch<T>(string sql, IParameter[] parameters)
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            IList<T> results = new List<T>();

            using (IDataReader dr = GetClient(PhysicDbName).Fetch(sql, parameters))
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

        public int Execute(string sql, IParameter[] parameters, bool masterOnly = true)
        {
            return GetClient(PhysicDbName).Execute(sql, parameters, masterOnly);
        }

        public IDataReader FetchBySp(string sp, IParameter[] parameters, bool masterOnly = true)
        {
            return GetClient(PhysicDbName).FetchBySp(sp, parameters, masterOnly);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <returns></returns>
        public IList<T> FetchBySp<T>(string sp, IParameter[] parameters)
        {
            Type type = typeof(T);

            SqlTable table = SqlTable.CreateInstance(type);

            IList<T> results = new List<T>();

            using (IDataReader dr = GetClient(PhysicDbName).FetchBySp(sp, parameters))
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

        public int ExecuteSp(string sp, IParameter[] parameters, bool masterOnly = true)
        {
            return GetClient(PhysicDbName).ExecuteSp(sp, parameters, masterOnly);
        }
    }
}
