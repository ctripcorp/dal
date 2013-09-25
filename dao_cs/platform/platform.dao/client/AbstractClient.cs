using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace platform.dao.client
{
    public abstract class AbstractClient : IClient
    {


        public virtual void SetCredential(string credential)
        {
            throw new NotImplementedException();
        }

        public virtual System.Data.IDataReader Fetch(string sql, param.StatementParameterCollection parameters, IDictionary extraOptions=null)
        {
            throw new NotImplementedException();
        }

        public virtual int Execute(string sql, param.StatementParameterCollection parameters, IDictionary extraOptions = null)
        {
            throw new NotImplementedException();
        }

        public virtual System.Data.IDataReader FetchBySp(string sp, param.StatementParameterCollection parameters, IDictionary extraOptions = null)
        {
            throw new NotImplementedException();
        }

        public virtual int ExecuteSp(string sp, param.StatementParameterCollection parameters, IDictionary extraOptions = null)
        {
            throw new NotImplementedException();
        }

        public virtual IList<T> Fetch<T>(orm.query.IQuery query, IDictionary extraOptions = null)
        {
            throw new NotImplementedException();
        }

        public virtual int Insert<T>(IList<T> lists, IDictionary extraOptions = null)
        {
            throw new NotImplementedException();
        }

        public virtual int Upadte<T>(IList<T> lists, orm.query.IQuery query, IDictionary extraOptions = null)
        {
            throw new NotImplementedException();
        }

        public virtual int Delete<T>(orm.query.IQuery query, IDictionary extraOptions = null)
        {
            throw new NotImplementedException();
        }
    }
}
