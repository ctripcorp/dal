using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools.Entity.DataModel;
using com.ctrip.platform.tools.IDao;

namespace com.ctrip.platform.tools
{
   /// <summary>
    /// 更多DALFx接口功能，请参阅DALFx Confluence，地址：
    /// http://conf.ctripcorp.com/display/ARCH/Dal+Fx+API
    /// </summary>
    public partial class SDP_Version_genDao : ISDP_Version_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AssembleDB");
        
        /// <summary>
        ///  插入SDP_Version_gen
        /// </summary>
        /// <param name="sDP_Version_gen">SDP_Version_gen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertSDP_Version_gen(SDP_Version_gen sDP_Version_gen)
        {
            try
            {
                Object result = baseDao.Insert<SDP_Version_gen>(sDP_Version_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDP_Version_gen时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改SDP_Version_gen
        /// </summary>
        /// <param name="sDP_Version_gen">SDP_Version_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateSDP_Version_gen(SDP_Version_gen sDP_Version_gen)
        {
            try
            {
                Object result = baseDao.Update<SDP_Version_gen>(sDP_Version_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDP_Version_gen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除SDP_Version_gen
        /// </summary>
        /// <param name="sDP_Version_gen">SDP_Version_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteSDP_Version_gen(SDP_Version_gen sDP_Version_gen)
        {
            try
            {
                Object result = baseDao.Delete<SDP_Version_gen>(sDP_Version_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDP_Version_gen时，访问Delete时出错", ex);
            }
        }
        
        

        /// <summary>
        /// 根据主键获取SDP_Version_gen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>SDP_Version_gen信息</returns>
        public SDP_Version_gen FindByPk(long iD )
        {
            try
            {
                return baseDao.GetByKey<SDP_Version_gen>(iD);
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDP_Version_genDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有SDP_Version_gen信息
        /// </summary>
        /// <returns>SDP_Version_gen列表</returns>
        public IList<SDP_Version_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<SDP_Version_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDP_Version_genDao时，访问GetAll时出错", ex);
            }
        }
        
        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        public long Count()
        {
            try
            {
                String sql = "SELECT count(1) from SDP_Version  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDP_Version_genDao时，访问Count时出错", ex);
            }
        }
        






		/// <summary>
        ///  GetTest
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        public IList<SDP_Version_gen> GetTest(long iD)
        {
        	try
            {
            	string sql = "SELECT TableName FROM SDP_Version WHERE  ID = @ID ";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.Int64, Value =iD });

                return baseDao.SelectList<SDP_Version_gen>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用SDP_Version_genDao时，访问GetTest时出错", ex);
            }
        }
        
    }
}