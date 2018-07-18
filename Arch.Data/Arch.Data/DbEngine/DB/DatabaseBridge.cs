using Arch.Data.Common.Util;
using Arch.Data.Common.Vi;
using Arch.Data.DbEngine.HA;
using Arch.Data.Properties;
using System;
using System.Data;

namespace Arch.Data.DbEngine.DB
{
    class DatabaseBridge
    {
        private DatabaseBridge() { }

        private static DatabaseBridge instance = new DatabaseBridge();

        public static DatabaseBridge Instance { get { return instance; } }

        /// <summary>
        /// 根据Statement，返回DataSet结果集
        /// </summary>
        /// <param name="statement"></param>
        /// <param name="tableNames"></param>
        /// <returns></returns>
        public DataSet ExecuteDataSet(Statement statement, params String[] tableNames)
        {
            try
            {
                LogManager.Logger.Next();
                if (BeanManager.GetMarkDownBean().AppIsMarkDown)
                    throw new DalMarkDownException(Resources.AppMarkDownException);
                var databases = DatabaseFactory.GetDatabasesByStatement(statement);
                return HAFactory.GetInstance(statement.DatabaseSet).ExecuteWithHa(db => db.ExecuteDataSet(statement, tableNames), databases);
            }
            finally
            {
                LogManager.Logger.Next();
            }
        }

        /// <summary>
        /// 根据Statement,执行增删改操作
        /// </summary>
        /// <param name="statement"></param>
        /// <returns></returns>
        public Int32 ExecuteNonQuery(Statement statement)
        {
            try
            {
                LogManager.Logger.Next();
                if (BeanManager.GetMarkDownBean().AppIsMarkDown)
                    throw new DalMarkDownException(Resources.AppMarkDownException);
                var databases = DatabaseFactory.GetDatabasesByStatement(statement);
                return HAFactory.GetInstance(statement.DatabaseSet).ExecuteWithHa(db => db.ExecuteNonQuery(statement), databases);
            }
            finally
            {
                LogManager.Logger.Next();
            }
        }

        /// <summary>
        /// 根据Statement，返回IDataReader形式的结果集
        /// </summary>
        /// <param name="statement"></param>
        /// <returns></returns>
        public IDataReader ExecuteReader(Statement statement)
        {
            try
            {
                LogManager.Logger.Next();
                if (BeanManager.GetMarkDownBean().AppIsMarkDown)
                    throw new DalMarkDownException(Resources.AppMarkDownException);
                var databases = DatabaseFactory.GetDatabasesByStatement(statement);
                return HAFactory.GetInstance(statement.DatabaseSet).ExecuteWithHa(db => db.ExecuteReader(statement), databases);
            }
            finally
            {
                LogManager.Logger.Next();
            }
        }

        /// <summary>
        /// 根据Statement，返回第一行第一列
        /// </summary>
        /// <param name="statement"></param>
        /// <returns></returns>
        public Object ExecuteScalar(Statement statement)
        {
            try
            {
                LogManager.Logger.Next();
                if (BeanManager.GetMarkDownBean().AppIsMarkDown)
                    throw new DalMarkDownException(Resources.AppMarkDownException);
                var databases = DatabaseFactory.GetDatabasesByStatement(statement);
                return HAFactory.GetInstance(statement.DatabaseSet).ExecuteWithHa(db => db.ExecuteScalar(statement), databases);
            }
            finally
            {
                LogManager.Logger.Next();
            }
        }

    }
}
