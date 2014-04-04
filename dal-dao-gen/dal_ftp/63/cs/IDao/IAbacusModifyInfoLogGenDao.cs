using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAbacusModifyInfoLogGenDao
        {

               /// <summary>
        ///  插入AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="abacusModifyInfoLogGen">AbacusModifyInfoLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAbacusModifyInfoLogGen(AbacusModifyInfoLogGen abacusModifyInfoLogGen);

        /// <summary>
        /// 修改AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="abacusModifyInfoLogGen">AbacusModifyInfoLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAbacusModifyInfoLogGen(AbacusModifyInfoLogGen abacusModifyInfoLogGen);

        /// <summary>
        /// 删除AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="abacusModifyInfoLogGen">AbacusModifyInfoLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAbacusModifyInfoLogGen(AbacusModifyInfoLogGen abacusModifyInfoLogGen);

        /// <summary>
        /// 删除AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        int DeleteAbacusModifyInfoLogGen(int logID);

        /// <summary>
        /// 根据主键获取AbacusModifyInfoLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusModifyInfoLogGen信息</returns>
        AbacusModifyInfoLogGen FindByPk(int logID);

        /// <summary>
        /// 获取所有AbacusModifyInfoLogGen信息
        /// </summary>
        /// <returns>AbacusModifyInfoLogGen列表</returns>
        IList<AbacusModifyInfoLogGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AbacusModifyInfoLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusModifyInfoLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AbacusModifyInfoLogGen> GetListByPage(AbacusModifyInfoLogGen obj, int pagesize, int pageNo);

        }
}