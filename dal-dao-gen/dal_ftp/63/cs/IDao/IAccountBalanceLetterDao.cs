using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IAccountBalanceLetterDao
        {

               /// <summary>
        ///  插入AccountBalanceLetter
        /// </summary>
        /// <param name="accountBalanceLetter">AccountBalanceLetter实体对象</param>
        /// <returns>状态代码</returns>
        int InsertAccountBalanceLetter(AccountBalanceLetter accountBalanceLetter);

        /// <summary>
        /// 修改AccountBalanceLetter
        /// </summary>
        /// <param name="accountBalanceLetter">AccountBalanceLetter实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateAccountBalanceLetter(AccountBalanceLetter accountBalanceLetter);

        /// <summary>
        /// 删除AccountBalanceLetter
        /// </summary>
        /// <param name="accountBalanceLetter">AccountBalanceLetter实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteAccountBalanceLetter(AccountBalanceLetter accountBalanceLetter);

        /// <summary>
        /// 删除AccountBalanceLetter
        /// </summary>
        /// <param name="recordId">@RecordId #></param>
        /// <returns>状态代码</returns>
        int DeleteAccountBalanceLetter(int recordId);

        /// <summary>
        /// 根据主键获取AccountBalanceLetter信息
        /// </summary>
        /// <param name="recordId"></param>
        /// <returns>AccountBalanceLetter信息</returns>
        AccountBalanceLetter FindByPk(int recordId);

        /// <summary>
        /// 获取所有AccountBalanceLetter信息
        /// </summary>
        /// <returns>AccountBalanceLetter列表</returns>
        IList<AccountBalanceLetter> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索AccountBalanceLetter，带翻页
        /// </summary>
        /// <param name="obj">AccountBalanceLetter实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<AccountBalanceLetter> GetListByPage(AccountBalanceLetter obj, int pagesize, int pageNo);

        }
}