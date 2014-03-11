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
    public partial class ManyTypesGenDao : IManyTypesGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("dao_test");
        
        /// <summary>
        ///  插入ManyTypesGen
        /// </summary>
        /// <param name="manyTypesGen">ManyTypesGen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertManyTypesGen(ManyTypesGen manyTypesGen)
        {
            try
            {
                Object result = baseDao.Insert<ManyTypesGen>(manyTypesGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用ManyTypesGen时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改ManyTypesGen
        /// </summary>
        /// <param name="manyTypesGen">ManyTypesGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateManyTypesGen(ManyTypesGen manyTypesGen)
        {
            try
            {
                Object result = baseDao.Update<ManyTypesGen>(manyTypesGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用ManyTypesGen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除ManyTypesGen
        /// </summary>
        /// <param name="manyTypesGen">ManyTypesGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteManyTypesGen(ManyTypesGen manyTypesGen)
        {
            try
            {
                Object result = baseDao.Delete<ManyTypesGen>(manyTypesGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用ManyTypesGen时，访问Delete时出错", ex);
            }
        }
        
        

        /// <summary>
        /// 根据主键获取ManyTypesGen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>ManyTypesGen信息</returns>
        public ManyTypesGen FindByPk(int id )
        {
            try
            {
                return baseDao.GetByKey<ManyTypesGen>(id);
            }
            catch (Exception ex)
            {
                throw new DalException("调用ManyTypesGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有ManyTypesGen信息
        /// </summary>
        /// <returns>ManyTypesGen列表</returns>
        public IList<ManyTypesGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<ManyTypesGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用ManyTypesGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from ManyTypes    ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用ManyTypesGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索ManyTypesGen，带翻页
        /// </summary>
        /// <param name="obj">ManyTypesGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<ManyTypesGen> GetListByPage(ManyTypesGen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                sbSql.Append(@"select Id, TinyIntCol, SmallIntCol, IntCol, BigIntCol, DecimalCol, DoubleCol, FloatCol, BitCol, CharCol, VarCharCol, DateCol, DateTimeCol, TimeCol, TimestampCol, YearCol, BinaryCol, BlobCol, LongBlobCol, MediumBlobCol, TinyBlobCol, VarBinaryCol, LongTextCol, MediumTextCol, TextCol, TinyTextCol from ManyTypes ");
                //包含查询条件
                //StringBuilder whereCondition = new StringBuilder();
                //if (!string.IsNullOrEmpty(obj.Name))
                //{
                //    //人名
                //    whereCondition.Append("Where Name like @Name ");
                //    dic.AddInParameter("@Name", DbType.String, "%" + obj.Name + "%");
                //}
                //sbSql.Append(whereCondition);
                sbSql.Append(" order by Id desc ");
                sbSql.Append( string.Format("limit {0}, {1} ", (pageNo - 1) * pagesize, pagesize));
                IList<ManyTypesGen> list = baseDao.SelectList<ManyTypesGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用ManyTypesGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}