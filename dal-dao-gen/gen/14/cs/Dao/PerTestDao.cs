using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using DAL.Entity.DataModel;

namespace DAL.Dao
{
    public partial class PerTestDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("PerformanceTest");

		/// <summary>
        ///  GetPerson
        /// </summary>
        /// <returns></returns>
        public IList<PerUser> GetPerson()
        {
        	try
            {
            	string sql = "select * from person";
                StatementParameterCollection parameters = new StatementParameterCollection();
				//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
				//return baseDao.SelectFirst<PerUser>(sql, parameters);
                return baseDao.SelectList<PerUser>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用PerTestDao时，访问GetPerson时出错", ex);
            }
        }

    }
}