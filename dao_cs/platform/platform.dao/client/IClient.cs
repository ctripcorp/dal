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
        IDataReader Fetch(string sql, params IParameter[] parameters);

        int Execute(string sql, params IParameter[] parameters);

        IDataReader FetchBySp(string sp, params IParameter[] parameters);

        int ExecuteSp(string sp, params IParameter[] parameters);

        //Begin orm

        //IList<T> Fetch<T>(string sql, params IParameter[] parameters);

        //int Insert<T>(T entity);

        //int BatchInsert<T>(IList<T> entities);

        //int Upadte<T>(string sql, params IParameter[] parameters);

        //int Delete<T>(T entity);

    }
}
