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

        IDataReader Fetch(string sql, StatementParameterCollection parameters);

        int Execute(string sql, StatementParameterCollection parameters);

        IDataReader FetchBySp(string sp, StatementParameterCollection parameters);

        int ExecuteSp(string sp, StatementParameterCollection parameters);

        //Begin orm

        IList<T> Fetch<T>(IQuery query);

        int Insert<T>(IList<T> lists);

        int Upadte<T>(IList<T> lists, IQuery query);

        int Delete<T>(IQuery query);

    }
}
