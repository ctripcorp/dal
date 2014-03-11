using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.shard.Entity.DataModel;
using com.ctrip.shard.Interface.IDao;

namespace com.ctrip.shard.Dao
{
   /// <summary>
    /// 更多DAL接口功能，请参阅DAL Confluence，地址：
    /// http://conf.ctripcorp.com/display/SysDev/Dal+Fx+API
    /// </summary>
    public partial class DjangoAdminLogGenDao : IDjangoAdminLogGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("CEPM");
        
        /// <summary>
        ///  插入DjangoAdminLogGen
        /// </summary>
        /// <param name="djangoAdminLogGen">DjangoAdminLogGen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertDjangoAdminLogGen(DjangoAdminLogGen djangoAdminLogGen)
        {
            try
            {
                Object result = baseDao.Insert<DjangoAdminLogGen>(djangoAdminLogGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用DjangoAdminLogGen时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改DjangoAdminLogGen
        /// </summary>
        /// <param name="djangoAdminLogGen">DjangoAdminLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateDjangoAdminLogGen(DjangoAdminLogGen djangoAdminLogGen)
        {
            try
            {
                Object result = baseDao.Update<DjangoAdminLogGen>(djangoAdminLogGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用DjangoAdminLogGen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除DjangoAdminLogGen
        /// </summary>
        /// <param name="djangoAdminLogGen">DjangoAdminLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteDjangoAdminLogGen(DjangoAdminLogGen djangoAdminLogGen)
        {
            try
            {
                Object result = baseDao.Delete<DjangoAdminLogGen>(djangoAdminLogGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用DjangoAdminLogGen时，访问Delete时出错", ex);
            }
        }
        
        

        /// <summary>
        /// 根据主键获取DjangoAdminLogGen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>DjangoAdminLogGen信息</returns>
        public DjangoAdminLogGen FindByPk(int id )
        {
            try
            {
                return baseDao.GetByKey<DjangoAdminLogGen>(id);
            }
            catch (Exception ex)
            {
                throw new DalException("调用DjangoAdminLogGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有DjangoAdminLogGen信息
        /// </summary>
        /// <returns>DjangoAdminLogGen列表</returns>
        public IList<DjangoAdminLogGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<DjangoAdminLogGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用DjangoAdminLogGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from django_admin_log    ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用DjangoAdminLogGenDao时，访问Count时出错", ex);
            }
        }
        






		/// <summary>
        ///  deletey
        /// </summary>
        /// <param name="action_flag"></param>
        /// <param name="id"></param>
        /// <returns></returns>
        public int deletey(short action_flag,int id)
        {
        	try
            {
            	string sql = "Delete FROM django_admin_log WHERE  action_flag = @action_flag  AND  id = @id ";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@action_flag", Direction = ParameterDirection.Input, DbType = DbType.Int16, Value =action_flag });
                parameters.Add(new StatementParameter{ Name = "@id", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value =id });

				return baseDao.ExecNonQuery(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用DjangoAdminLogGenDao时，访问deletey时出错", ex);
            }
        }
        
    }
}