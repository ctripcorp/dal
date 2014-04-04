using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAbacusCancelPNRLogGenDao
        {

               /// <summary>
        ///  插入AbacusCancelPNRLogGen
        /// </summary>
        /// <param name="abacusCancelPNRLogGen">AbacusCancelPNRLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAbacusCancelPNRLogGen(AbacusCancelPNRLogGen abacusCancelPNRLogGen);

        /// <summary>
        /// 修改AbacusCancelPNRLogGen
        /// </summary>
        /// <param name="abacusCancelPNRLogGen">AbacusCancelPNRLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAbacusCancelPNRLogGen(AbacusCancelPNRLogGen abacusCancelPNRLogGen);

        /// <summary>
        /// 删除AbacusCancelPNRLogGen
        /// </summary>
        /// <param name="abacusCancelPNRLogGen">AbacusCancelPNRLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAbacusCancelPNRLogGen(AbacusCancelPNRLogGen abacusCancelPNRLogGen);

        /// <summary>
        /// 删除AbacusCancelPNRLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        int DeleteAbacusCancelPNRLogGen(int logID);

        /// <summary>
        /// 根据主键获取AbacusCancelPNRLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusCancelPNRLogGen信息</returns>
        AbacusCancelPNRLogGen FindByPk(int logID);

        /// <summary>
        /// 获取所有AbacusCancelPNRLogGen信息
        /// </summary>
        /// <returns>AbacusCancelPNRLogGen列表</returns>
        IList<AbacusCancelPNRLogGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AbacusCancelPNRLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusCancelPNRLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AbacusCancelPNRLogGen> GetListByPage(AbacusCancelPNRLogGen obj, int pagesize, int pageNo);

        }
}