using System;
using System.Collections.Generic;
using com.ctrip.platform.uat.Entity.DataModel;

namespace com.ctrip.platform.uat.Interface.IDao
{
	public partial interface IMoneyTest_genDao
	{

	       /// <summary>
        ///  插入MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertMoneyTest_gen(MoneyTest_gen moneyTest_gen);

        /// <summary>
        /// 修改MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateMoneyTest_gen(MoneyTest_gen moneyTest_gen);

        /// <summary>
        /// 删除MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteMoneyTest_gen(MoneyTest_gen moneyTest_gen);


        /// <summary>
        /// 根据主键获取MoneyTest_gen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>MoneyTest_gen信息</returns>
        MoneyTest_gen FindByPk(int id);

        /// <summary>
        /// 获取所有MoneyTest_gen信息
        /// </summary>
        /// <returns>MoneyTest_gen列表</returns>
        IList<MoneyTest_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索MoneyTest_gen，带翻页
        /// </summary>
        /// <param name="obj">MoneyTest_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<MoneyTest_gen> GetListByPage(MoneyTest_gen obj, int pagesize, int pageNo);

	}
}