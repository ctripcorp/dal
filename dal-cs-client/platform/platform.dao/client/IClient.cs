using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using platform.dao.param;
using System.Collections;

namespace platform.dao.client
{
    public interface IClient
    {
        IDataReader Fetch(string sql, IParameter[] parameters, bool masterOnly = true);

        int Execute(string sql, IParameter[] parameters, bool masterOnly = true);

        IDataReader FetchBySp(string sp, IParameter[] parameters, bool masterOnly = true);

        int ExecuteSp(string sp, IParameter[] parameters, bool masterOnly = true);

        //Begin orm

        //IList<T> Fetch<T>(string sql, params IParameter[] parameters);

        //int Insert<T>(T entity);

        //int BatchInsert<T>(IList<T> entities);

        //int Upadte<T>(string sql, params IParameter[] parameters);

        //int Delete<T>(T entity);

    }
}
