using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;
using platform.dao.providers;
using System.Data.Common;
using platform.dao.param;
using System.Data;
using System.Collections;

namespace platform.dao.client
{
    public class DbClient : AbstractClient
    {

        private IDatabaseProvider databaseProvider;

        private DbConnection connection;

        public DbClient(string providerType, string credential)
        {
            this.databaseProvider = Activator.CreateInstance(Type.GetType(providerType)) as IDatabaseProvider;
            this.connection = this.databaseProvider.CreateConnection();
            this.connection.ConnectionString = credential;
        }

        ///// <summary>
        ///// 设置连接字符串（或者DAS的用户名和密码对）
        ///// </summary>
        ///// <param name="credential"></param>
        ///// <returns></returns>
        //public override void SetCredential(string credential)
        //{
        //    this.connection.ConnectionString = credential;
        //}

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override IDataReader Fetch(string sql, params IParameter[] parameters)
        {
            if (this.connection.State != System.Data.ConnectionState.Open)
            {
                this.connection.Open();
            }

            DbCommand command = this.databaseProvider.CreateCommand();

            command.CommandText = sql;
            command.CommandType = System.Data.CommandType.Text;

            foreach (StatementParameter p in parameters)
            {
                DbParameter param = command.CreateParameter();

                param.ParameterName = this.databaseProvider.CreateParameterName(p.Name);
                param.DbType = p.DbType;
                param.Size = p.Size;
                param.Value = p.Value;
                param.Direction = p.Direction;
                param.IsNullable = p.IsNullable;

                command.Parameters.Add(param);
            }

            command.Connection = this.connection;

            IDataReader dr = command.ExecuteReader();

            command.Dispose();

            return dr;

        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override int Execute(string sql, params IParameter[] parameters)
        {
            if (this.connection.State != System.Data.ConnectionState.Open)
            {
                this.connection.Open();
            }

            DbCommand command = this.databaseProvider.CreateCommand();

            command.CommandText = sql;
            command.CommandType = System.Data.CommandType.Text;

            foreach (StatementParameter p in parameters)
            {
                DbParameter param = command.CreateParameter();

                param.ParameterName = this.databaseProvider.CreateParameterName(p.Name);
                param.DbType = p.DbType;
                param.Size = p.Size;
                param.Value = p.Value;
                param.Direction = p.Direction;
                param.IsNullable = p.IsNullable;

                command.Parameters.Add(param);
            }

            command.Connection = this.connection;

            int count = command.ExecuteNonQuery();

            command.Dispose();

            return count;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override IDataReader FetchBySp(string sp, params IParameter[] parameters)
        {
            if (this.connection.State != System.Data.ConnectionState.Open)
            {
                this.connection.Open();
            }

            DbCommand command = this.databaseProvider.CreateCommand();

            command.CommandText = sp;
            command.CommandType = System.Data.CommandType.StoredProcedure;

            foreach (StatementParameter p in parameters)
            {
                DbParameter param = command.CreateParameter();

                param.ParameterName = this.databaseProvider.CreateParameterName(p.Name);
                param.DbType = p.DbType;
                param.Size = p.Size;
                param.Value = p.Value;
                param.Direction = p.Direction;
                param.IsNullable = p.IsNullable;

                command.Parameters.Add(param);
            }

            command.Connection = this.connection;

            IDataReader dr = command.ExecuteReader();

            command.Dispose();

            return dr;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override int ExecuteSp(string sp, params IParameter[] parameters)
        {
            if (this.connection.State != System.Data.ConnectionState.Open)
            {
                this.connection.Open();
            }

            DbCommand command = this.databaseProvider.CreateCommand();

            command.CommandText = sp;
            command.CommandType = System.Data.CommandType.StoredProcedure;

            foreach (StatementParameter p in parameters)
            {
                DbParameter param = command.CreateParameter();

                param.ParameterName = this.databaseProvider.CreateParameterName(p.Name);
                param.DbType = p.DbType;
                param.Size = p.Size;
                param.Value = p.Value;
                param.Direction = p.Direction;
                param.IsNullable = p.IsNullable;

                command.Parameters.Add(param);
            }

            command.Connection = this.connection;

            int count = command.ExecuteNonQuery();

            command.Dispose();

            return count;
        }
       
    }
}
