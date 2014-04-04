using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAbacusCreateSegmentLogGenDao
        {

               /// <summary>
        ///  插入AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="abacusCreateSegmentLogGen">AbacusCreateSegmentLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAbacusCreateSegmentLogGen(AbacusCreateSegmentLogGen abacusCreateSegmentLogGen);

        /// <summary>
        /// 修改AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="abacusCreateSegmentLogGen">AbacusCreateSegmentLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAbacusCreateSegmentLogGen(AbacusCreateSegmentLogGen abacusCreateSegmentLogGen);

        /// <summary>
        /// 删除AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="abacusCreateSegmentLogGen">AbacusCreateSegmentLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAbacusCreateSegmentLogGen(AbacusCreateSegmentLogGen abacusCreateSegmentLogGen);

        /// <summary>
        /// 删除AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        int DeleteAbacusCreateSegmentLogGen(int logID);

        /// <summary>
        /// 根据主键获取AbacusCreateSegmentLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusCreateSegmentLogGen信息</returns>
        AbacusCreateSegmentLogGen FindByPk(int logID);

        /// <summary>
        /// 获取所有AbacusCreateSegmentLogGen信息
        /// </summary>
        /// <returns>AbacusCreateSegmentLogGen列表</returns>
        IList<AbacusCreateSegmentLogGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AbacusCreateSegmentLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusCreateSegmentLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AbacusCreateSegmentLogGen> GetListByPage(AbacusCreateSegmentLogGen obj, int pagesize, int pageNo);

        }
}