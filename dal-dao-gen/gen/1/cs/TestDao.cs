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
				//���ֻ��Ҫһ����¼������ʹ��limit 1����top 1����ʹ��SelectFirst�������
				//return baseDao.SelectFirst<Test>(sql, parameters);
                return baseDao.SelectList<Test>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("����TestDaoʱ������selectAbcʱ����", ex);
            }
        }

    }
}