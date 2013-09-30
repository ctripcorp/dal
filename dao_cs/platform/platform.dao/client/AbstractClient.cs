using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using platform.dao.param;

namespace platform.dao.client
{
    public abstract class AbstractClient : IClient
    {

        public virtual void SetCredential(string credential)
        {
            throw new NotImplementedException();
        }

        public virtual System.Data.IDataReader Fetch(string sql, params IParameter[] parameters)
        {
            throw new NotImplementedException();
        }

        public virtual int Execute(string sql, params IParameter[] parameters)
        {
            throw new NotImplementedException();
        }

        public virtual System.Data.IDataReader FetchBySp(string sp, params IParameter[] parameters)
        {
            throw new NotImplementedException();
        }

        public virtual int ExecuteSp(string sp, params IParameter[] parameters)
        {
            throw new NotImplementedException();
        }

        public virtual IList<T> Fetch<T>(string sql, params IParameter[] parameters)
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

        public virtual int Upadte<T>(string sql, params IParameter[] parameters)
        {
            throw new NotImplementedException();
        }

        public virtual int Delete<T>(T entity)
        {
            throw new NotImplementedException();
        }
    }
}
