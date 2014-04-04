using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface IFltOrdersTmpDao
        {

               /// <summary>
        ///  插入FltOrdersTmp
        /// </summary>
        /// <param name="fltOrdersTmp">FltOrdersTmp实体对象</param>
        /// <returns>状态代码</returns>
        int InsertFltOrdersTmp(FltOrdersTmp fltOrdersTmp);

        /// <summary>
        /// 修改FltOrdersTmp
        /// </summary>
        /// <param name="fltOrdersTmp">FltOrdersTmp实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateFltOrdersTmp(FltOrdersTmp fltOrdersTmp);

        /// <summary>
        /// 删除FltOrdersTmp
        /// </summary>
        /// <param name="fltOrdersTmp">FltOrdersTmp实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteFltOrdersTmp(FltOrdersTmp fltOrdersTmp);

        /// <summary>
        /// 删除FltOrdersTmp
        /// </summary>
        /// <param name="recordId">@RecordId #></param>
        /// <returns>状态代码</returns>
        int DeleteFltOrdersTmp(int recordId);

        /// <summary>
        /// 根据主键获取FltOrdersTmp信息
        /// </summary>
        /// <param name="recordId"></param>
        /// <returns>FltOrdersTmp信息</returns>
        FltOrdersTmp FindByPk(int recordId);

        /// <summary>
        /// 获取所有FltOrdersTmp信息
        /// </summary>
        /// <returns>FltOrdersTmp列表</returns>
        IList<FltOrdersTmp> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索FltOrdersTmp，带翻页
        /// </summary>
        /// <param name="obj">FltOrdersTmp实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<FltOrdersTmp> GetListByPage(FltOrdersTmp obj, int pagesize, int pageNo);

        }
}