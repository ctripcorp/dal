using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAbacusParaGenDao
        {

               /// <summary>
        ///  插入AbacusParaGen
        /// </summary>
        /// <param name="abacusParaGen">AbacusParaGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAbacusParaGen(AbacusParaGen abacusParaGen);

        /// <summary>
        /// 修改AbacusParaGen
        /// </summary>
        /// <param name="abacusParaGen">AbacusParaGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAbacusParaGen(AbacusParaGen abacusParaGen);

        /// <summary>
        /// 删除AbacusParaGen
        /// </summary>
        /// <param name="abacusParaGen">AbacusParaGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAbacusParaGen(AbacusParaGen abacusParaGen);

        /// <summary>
        /// 删除AbacusParaGen
        /// </summary>
        /// <param name="paraID">@ParaID #></param>
        /// <returns>状态代码</returns>
        int DeleteAbacusParaGen(int paraID);

        /// <summary>
        /// 根据主键获取AbacusParaGen信息
        /// </summary>
        /// <param name="paraID"></param>
        /// <returns>AbacusParaGen信息</returns>
        AbacusParaGen FindByPk(int paraID);

        /// <summary>
        /// 获取所有AbacusParaGen信息
        /// </summary>
        /// <returns>AbacusParaGen列表</returns>
        IList<AbacusParaGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AbacusParaGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusParaGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AbacusParaGen> GetListByPage(AbacusParaGen obj, int pagesize, int pageNo);

        }
}