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
    public partial class spAPersond_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("");

        /// <summary>
        ///  执行SPspAPersond_gen
        /// </summary>
        /// <param name="spAPersond_gen">spAPersond_gen实体对象</param>
        /// <returns>影响的行数</returns>
        public int ExecspAPersond_gen(spAPersond_gen spAPersond_gen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.UInt32, Value = spAPersond_gen.ID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("dbo.spA_Person_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用spAPersond_genDao时，访问ExecspAPersond_gen时出错", ex);
            }

       }

    }
}