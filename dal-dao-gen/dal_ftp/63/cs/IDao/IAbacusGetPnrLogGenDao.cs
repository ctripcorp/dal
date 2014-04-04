using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAbacusGetPnrLogGenDao
        {

               /// <summary>
        ///  插入AbacusGetPnrLogGen
        /// </summary>
        /// <param name="abacusGetPnrLogGen">AbacusGetPnrLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAbacusGetPnrLogGen(AbacusGetPnrLogGen abacusGetPnrLogGen);

        /// <summary>
        /// 修改AbacusGetPnrLogGen
        /// </summary>
        /// <param name="abacusGetPnrLogGen">AbacusGetPnrLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAbacusGetPnrLogGen(AbacusGetPnrLogGen abacusGetPnrLogGen);

        /// <summary>
        /// 删除AbacusGetPnrLogGen
        /// </summary>
        /// <param name="abacusGetPnrLogGen">AbacusGetPnrLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAbacusGetPnrLogGen(AbacusGetPnrLogGen abacusGetPnrLogGen);

        /// <summary>
        /// 删除AbacusGetPnrLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        int DeleteAbacusGetPnrLogGen(int logID);

        /// <summary>
        /// 根据主键获取AbacusGetPnrLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusGetPnrLogGen信息</returns>
        AbacusGetPnrLogGen FindByPk(int logID);

        /// <summary>
        /// 获取所有AbacusGetPnrLogGen信息
        /// </summary>
        /// <returns>AbacusGetPnrLogGen列表</returns>
        IList<AbacusGetPnrLogGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AbacusGetPnrLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusGetPnrLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AbacusGetPnrLogGen> GetListByPage(AbacusGetPnrLogGen obj, int pagesize, int pageNo);

        }
}