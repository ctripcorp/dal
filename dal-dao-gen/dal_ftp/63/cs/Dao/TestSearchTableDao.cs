
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
    public partial class TestSearchTableDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("");

        /// <summary>
        ///  执行SPTestSearchTable
        /// </summary>
        /// <param name="testSearchTable">TestSearchTable实体对象</param>
        /// <returns>影响的行数</returns>
        public int ExecTestSearchTable(TestSearchTable testSearchTable)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("CN1\jian_chen.Test_SearchTable", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用TestSearchTableDao时，访问ExecTestSearchTable时出错", ex);
            }

       }

    }
}
