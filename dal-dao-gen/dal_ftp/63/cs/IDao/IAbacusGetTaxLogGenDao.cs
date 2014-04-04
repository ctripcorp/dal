using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAbacusGetTaxLogGenDao
        {

               /// <summary>
        ///  插入AbacusGetTaxLogGen
        /// </summary>
        /// <param name="abacusGetTaxLogGen">AbacusGetTaxLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAbacusGetTaxLogGen(AbacusGetTaxLogGen abacusGetTaxLogGen);

        /// <summary>
        /// 修改AbacusGetTaxLogGen
        /// </summary>
        /// <param name="abacusGetTaxLogGen">AbacusGetTaxLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAbacusGetTaxLogGen(AbacusGetTaxLogGen abacusGetTaxLogGen);

        /// <summary>
        /// 删除AbacusGetTaxLogGen
        /// </summary>
        /// <param name="abacusGetTaxLogGen">AbacusGetTaxLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAbacusGetTaxLogGen(AbacusGetTaxLogGen abacusGetTaxLogGen);

        /// <summary>
        /// 删除AbacusGetTaxLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        int DeleteAbacusGetTaxLogGen(int logID);

        /// <summary>
        /// 根据主键获取AbacusGetTaxLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusGetTaxLogGen信息</returns>
        AbacusGetTaxLogGen FindByPk(int logID);

        /// <summary>
        /// 获取所有AbacusGetTaxLogGen信息
        /// </summary>
        /// <returns>AbacusGetTaxLogGen列表</returns>
        IList<AbacusGetTaxLogGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AbacusGetTaxLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusGetTaxLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AbacusGetTaxLogGen> GetListByPage(AbacusGetTaxLogGen obj, int pagesize, int pageNo);

        }
}