using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAccountcheckDao
        {

               /// <summary>
        ///  插入Accountcheck
        /// </summary>
        /// <param name="accountcheck">Accountcheck实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAccountcheck(Accountcheck accountcheck);

        /*由于没有PK，不能生成Update和Delete方法
        /// <summary>
        /// 修改Accountcheck
        /// </summary>
        /// <param name="accountcheck">Accountcheck实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAccountcheck(Accountcheck accountcheck);

        /// <summary>
        /// 删除Accountcheck
        /// </summary>
        /// <param name="accountcheck">Accountcheck实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAccountcheck(Accountcheck accountcheck);

        */


        /// <summary>
        /// 获取所有Accountcheck信息
        /// </summary>
        /// <returns>Accountcheck列表</returns>
        IList<Accountcheck> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索Accountcheck，带翻页
        /// </summary>
        /// <param name="obj">Accountcheck实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<Accountcheck> GetListByPage(Accountcheck obj, int pagesize, int pageNo);

        }
}