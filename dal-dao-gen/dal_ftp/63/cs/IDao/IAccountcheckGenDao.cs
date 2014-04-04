using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAccountcheckGenDao
        {

               /// <summary>
        ///  插入AccountcheckGen
        /// </summary>
        /// <param name="accountcheckGen">AccountcheckGen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertAccountcheckGen(AccountcheckGen accountcheckGen);

        /*由于没有PK，不能生成Update和Delete方法
        /// <summary>
        /// 修改AccountcheckGen
        /// </summary>
        /// <param name="accountcheckGen">AccountcheckGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAccountcheckGen(AccountcheckGen accountcheckGen);

        /// <summary>
        /// 删除AccountcheckGen
        /// </summary>
        /// <param name="accountcheckGen">AccountcheckGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAccountcheckGen(AccountcheckGen accountcheckGen);

        */


        /// <summary>
        /// 获取所有AccountcheckGen信息
        /// </summary>
        /// <returns>AccountcheckGen列表</returns>
        IList<AccountcheckGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();


        /// <summary>
        ///  fffff
        /// </summary>
        /// <param name="limited"></param>
        /// <returns></returns>
        IList<AccountcheckGen> fffff(decimal limited);
        }
}