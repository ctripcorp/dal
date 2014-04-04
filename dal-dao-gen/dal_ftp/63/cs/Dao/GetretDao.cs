
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Dao
{
    public partial class GetretDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("");

        /// <summary>
        ///  执行SPGetret
        /// </summary>
        /// <param name="getret">Getret实体对象</param>
        /// <returns>影响的行数</returns>
        public int ExecGetret(Getret getret)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@para1", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = getret.Para1});
                parameters.Add(new StatementParameter{ Name = "@para2", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = getret.Para2});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("CN1\jian_chen.getret", parameters);

                getret.Para1 = (int)parameters["@para1"].Value;
                getret.Para2 = (int)parameters["@para2"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用GetretDao时，访问ExecGetret时出错", ex);
            }

       }

    }
}
