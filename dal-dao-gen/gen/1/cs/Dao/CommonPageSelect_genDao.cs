
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools.Entity.DataModel;

namespace com.ctrip.platform.tools.Dao
{
    public partial class CommonPageSelect_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("");

        /// <summary>
        ///  执行SPCommonPageSelect_gen
        /// </summary>
        /// <param name="commonPageSelect_gen">CommonPageSelect_gen实体对象</param>
        /// <returns>影响的行数</returns>
        public int ExecCommonPageSelect_gen(CommonPageSelect_gen commonPageSelect_gen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@SqlTable", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = commonPageSelect_gen.Sqltable});
                parameters.Add(new StatementParameter{ Name = "@SqlColumn", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = commonPageSelect_gen.Sqlcolumn});
                parameters.Add(new StatementParameter{ Name = "@SqlWhere", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = commonPageSelect_gen.Sqlwhere});
                parameters.Add(new StatementParameter{ Name = "@pagenum", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = commonPageSelect_gen.Pagenum});
                parameters.Add(new StatementParameter{ Name = "@beginline", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = commonPageSelect_gen.Beginline});
                parameters.Add(new StatementParameter{ Name = "@SqlPK", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = commonPageSelect_gen.Sqlpk});
                parameters.Add(new StatementParameter{ Name = "@SqlOrder", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = commonPageSelect_gen.Sqlorder});
                parameters.Add(new StatementParameter{ Name = "@Count", Direction = ParameterDirection.InputOutput, DbType = DbType.Int64, Value = commonPageSelect_gen.Count});
                parameters.Add(new StatementParameter{ Name = "@pageCount", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = commonPageSelect_gen.Pagecount});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("dbo.CommonPageSelect", parameters);

                commonPageSelect_gen.Count = (long)parameters["@Count"].Value;
                commonPageSelect_gen.Pagecount = (int)parameters["@pageCount"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用CommonPageSelect_genDao时，访问ExecCommonPageSelect_gen时出错", ex);
            }

       }

    }
}
