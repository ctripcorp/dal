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
    public partial class QueryDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("daogen");

		/// <summary>
        ///  getServerById
        /// </summary>
        /// <param name="1"></param>
        /// <returns></returns>
        public IList<Query> getServerById(int 1)
        {
        	try
            {
            	string sql = "select id, driver, server,port,domain, user,password, db_type from daogen.data_source WHERE id = ?";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@1", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value =1 });
				//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
				//return baseDao.SelectFirst<Query>(sql, parameters);
                return baseDao.SelectList<Query>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用QueryDao时，访问getServerById时出错", ex);
            }
        }

    }
}