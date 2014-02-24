
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
    public partial class spAPersoni_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("");

        /// <summary>
        ///  执行SPspAPersoni_gen
        /// </summary>
        /// <param name="spAPersoni_gen">spAPersoni_gen实体对象</param>
        /// <returns>影响的行数</returns>
        public int ExecspAPersoni_gen(spAPersoni_gen spAPersoni_gen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = spAPersoni_gen.Id});
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = spAPersoni_gen.Name});
                parameters.Add(new StatementParameter{ Name = "@Age", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = spAPersoni_gen.Age});
                parameters.Add(new StatementParameter{ Name = "@Birth", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = spAPersoni_gen.Birth});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("dbo.spA_Person_i", parameters);

                spAPersoni_gen.Id = (int)parameters["@ID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用spAPersoni_genDao时，访问ExecspAPersoni_gen时出错", ex);
            }

       }

    }
}
