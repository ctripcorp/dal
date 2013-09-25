using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using platform.dao.param;
using platform.dao.orm.query;
using System.Collections;

namespace platform.dao.client
{
    public interface IClient
    {
        void SetCredential(string credential);

        IDataReader Fetch(string sql, StatementParameterCollection parameters, IDictionary extraOptions=null);

        int Execute(string sql, StatementParameterCollection parameters, IDictionary extraOptions = null);

        IDataReader FetchBySp(string sp, StatementParameterCollection parameters, IDictionary extraOptions = null);

        int ExecuteSp(string sp, StatementParameterCollection parameters, IDictionary extraOptions = null);

        //Begin orm

        IList<T> Fetch<T>(IQuery query, IDictionary extraOptions = null);

        int Insert<T>(IList<T> lists, IDictionary extraOptions = null);

        int Upadte<T>(IList<T> lists, IQuery query, IDictionary extraOptions = null);

        int Delete<T>(IQuery query, IDictionary extraOptions = null);

    }
}
