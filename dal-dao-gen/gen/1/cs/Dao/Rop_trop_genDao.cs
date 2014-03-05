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
    public partial class Rop_trop_genDao : IRop_trop_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("rmdbtest");
        
        /// <summary>
        ///  插入Rop_trop_gen
        /// </summary>
        /// <param name="rop_trop_gen">Rop_trop_gen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertRop_trop_gen(Rop_trop_gen rop_trop_gen)
        {
            try
            {
                Object result = baseDao.Insert<Rop_trop_gen>(rop_trop_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Rop_trop_gen时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改Rop_trop_gen
        /// </summary>
        /// <param name="rop_trop_gen">Rop_trop_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateRop_trop_gen(Rop_trop_gen rop_trop_gen)
        {
            try
            {
                Object result = baseDao.Update<Rop_trop_gen>(rop_trop_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Rop_trop_gen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除Rop_trop_gen
        /// </summary>
        /// <param name="rop_trop_gen">Rop_trop_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteRop_trop_gen(Rop_trop_gen rop_trop_gen)
        {
            try
            {
                Object result = baseDao.Delete<Rop_trop_gen>(rop_trop_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Rop_trop_gen时，访问Delete时出错", ex);
            }
        }
        
        

        /// <summary>
        /// 根据主键获取Rop_trop_gen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>Rop_trop_gen信息</returns>
        public Rop_trop_gen FindByPk(int id )
        {
            try
            {
                return baseDao.GetByKey<Rop_trop_gen>(id);
            }
            catch (Exception ex)
            {
                throw new DalException("调用Rop_trop_genDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有Rop_trop_gen信息
        /// </summary>
        /// <returns>Rop_trop_gen列表</returns>
        public IList<Rop_trop_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<Rop_trop_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用Rop_trop_genDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from rop_trop    ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Rop_trop_genDao时，访问Count时出错", ex);
            }
        }
        






		/// <summary>
        ///  tableTest
        /// </summary>
        /// <param name="createdtime"></param>
        /// <returns></returns>
        public IList<Rop_trop_gen> tableTest(DateTime createdtime)
        {
        	try
            {
            	string sql = "SELECT id,createdtime,rop_id FROM rop_trop WHERE  createdtime = @createdtime ";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@createdtime", Direction = ParameterDirection.Input, DbType = DbType.DateTime2, Value =createdtime });

                return baseDao.SelectList<Rop_trop_gen>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用Rop_trop_genDao时，访问tableTest时出错", ex);
            }
        }
        
    }
}