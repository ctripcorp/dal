
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
    public partial class SpTPersoni_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("");

        /// <summary>
        ///  执行SPSpTPersoni_gen
        /// </summary>
        /// <param name="spTPersoni_gen">SpTPersoni_gen实体对象</param>
        /// <returns>影响的行数</returns>
        public int ExecSpTPersoni_gen(SpTPersoni_gen spTPersoni_gen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@TVP_Person", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value = spTPersoni_gen.Tvp_person});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("dbo.spT_Person_i", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SpTPersoni_genDao时，访问ExecSpTPersoni_gen时出错", ex);
            }

       }

    }
}
