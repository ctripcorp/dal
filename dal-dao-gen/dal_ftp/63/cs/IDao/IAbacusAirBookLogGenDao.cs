using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAbacusAirBookLogGenDao
        {

               /// <summary>
        ///  插入AbacusAirBookLogGen
        /// </summary>
        /// <param name="abacusAirBookLogGen">AbacusAirBookLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAbacusAirBookLogGen(AbacusAirBookLogGen abacusAirBookLogGen);

        /// <summary>
        /// 修改AbacusAirBookLogGen
        /// </summary>
        /// <param name="abacusAirBookLogGen">AbacusAirBookLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAbacusAirBookLogGen(AbacusAirBookLogGen abacusAirBookLogGen);

        /// <summary>
        /// 删除AbacusAirBookLogGen
        /// </summary>
        /// <param name="abacusAirBookLogGen">AbacusAirBookLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAbacusAirBookLogGen(AbacusAirBookLogGen abacusAirBookLogGen);

        /// <summary>
        /// 删除AbacusAirBookLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        int DeleteAbacusAirBookLogGen(int logID);

        /// <summary>
        /// 根据主键获取AbacusAirBookLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusAirBookLogGen信息</returns>
        AbacusAirBookLogGen FindByPk(int logID);

        /// <summary>
        /// 获取所有AbacusAirBookLogGen信息
        /// </summary>
        /// <returns>AbacusAirBookLogGen列表</returns>
        IList<AbacusAirBookLogGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AbacusAirBookLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusAirBookLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AbacusAirBookLogGen> GetListByPage(AbacusAirBookLogGen obj, int pagesize, int pageNo);

        }
}