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
    public partial class TestDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("PerformanceTest");

		/// <summary>
        ///  selectAbc
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public IList<Test> selectAbc(uint id)
        {
        	try
            {
            	string sql = "select * from person where id = @id";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@id", Direction = ParameterDirection.Input, DbType = DbType.UInt32, Value =id });
				//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
				//return baseDao.SelectFirst<Test>(sql, parameters);
                return baseDao.SelectList<Test>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用TestDao时，访问selectAbc时出错", ex);
            }
        }

    }
}